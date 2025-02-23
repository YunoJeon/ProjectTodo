import React, {useEffect, useState} from "react";
import CommentList, {CommentResponseDto} from "./commentList";
import api from "../services/api";
import {Divider, message, Typography} from "antd";
import CommentInput from "./CommentInput";

interface CommentSectionProps {
  todoId: number;
  visible: boolean;
}

const CommentSection: React.FC<CommentSectionProps> = ({todoId, visible}) => {
  const [comments, setComments] = useState<CommentResponseDto[]>([]);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const pageSize = 10;

  const fetchComments = async (currentPage: number) => {
    if (!hasMore || loading) return;
    setLoading(true);
    try {
      const response = await api.get(`/todos/${todoId}/comments`,
          {params: {page: currentPage, pageSize}});
      const newComments: CommentResponseDto[] = response.data.content || [];
      const totalElements: number = response.data.totalElements;

      setComments((prev) => {
        const updated = currentPage === 1 ? newComments : [...prev, ...newComments];
        if (updated.length >= totalElements) {
          setHasMore(false);
        }
        return updated;
      });

      setPage(currentPage + 1);
    } catch (error) {
      console.error("댓글 조회 실패", error);
      message.error("댓글을 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (visible) {
      setPage(1);
      setHasMore(true);
      setComments([]);
      fetchComments(1);
    }
  }, [todoId, visible]);

  const handleDeleteComment = (commentId: number) => {
    api.delete(`/todos/${todoId}/comments/${commentId}`)
    .then(() => {
      message.success("댓글이 삭제되었습니다.");
      setComments((prev) => prev.filter(comment => comment.commentId !== commentId));
    })
    .catch((error) => {
      if (error.response && error.status === 403) {
        console.error("댓글 삭제 실패", error);
        message.error("삭제 권한이 없습니다.");
      } else {
        message.error("댓글 삭제에 실패하였습니다.");
      }
    })
  }

  const handleEditComment = (commentId: number, newContent: string) => {
    api.put(`/todos/${todoId}/comments/${commentId}`, {content: newContent})
    .then(() => {
      message.success("댓글이 수정되었습니다.");
      setPage(1);
      setHasMore(true);
      fetchComments(1);
    })
    .catch((error) => {
      if (error.response && error.status === 403) {
        console.error("댓글 수정 실패", error);
        message.error("수정 권한이 없습니다.");
      } else {
        message.error("댓글 수정에 실패하였습니다.");
      }
    })
  }

  const handleCommentAdded = () => {
    setPage(1);
    setHasMore(true);
    fetchComments(1);
  }

  return (
      <div>
        <Divider/>
        <Typography.Title level={5}>댓글💬</Typography.Title>
        {comments.length === 0 && !loading ? (
            <Typography.Text>아직 등록된 댓글이 없습니다.</Typography.Text>
        ) : (
            <CommentList
                todoId={todoId}
                comments={comments}
                loadMore={() => fetchComments(page)}
                hasMore={hasMore}
                onDelete={handleDeleteComment}
                onEdit={handleEditComment}
            />
        )}
        <CommentInput
            todoId={todoId}
            onCommentAdded={handleCommentAdded}
        />
      </div>
  );
};

export default CommentSection;