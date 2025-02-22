import React, {useCallback, useEffect, useRef, useState} from "react";
import {Avatar, Button, Input, Typography} from "antd";
import {CloseOutlined, DeleteOutlined, EditOutlined, CheckOutlined} from "@ant-design/icons";
import moment from "moment";
import {VariableSizeList as ListWindow, ListChildComponentProps} from "react-window";
import InfiniteLoader from "react-window-infinite-loader";

export interface CommentResponseDto {
  commentId: number;
  commentAuthorId: number;
  commentAuthorProfileImageUrl: string;
  commentAuthorName: string;
  content: string;
  createdAt: string;
}

export interface CommentListProps {
  todoId: number;
  comments: CommentResponseDto[];
  loadMore: () => Promise<void>;
  hasMore: boolean;
  onDelete: (commentId: number) => void;
  onEdit: (commentId: number, newContent: string) => void;
}

interface CommentRowData {
  todoId: number;
  comments: CommentResponseDto[];
  onDelete: (commentId: number) => void;
  onEdit: (commentId: number, newContent: string) => void;
}

const CommentRow: React.FC<ListChildComponentProps<CommentRowData>> = ({index, style, data}) => {

  const [isEditing, setIsEditing] = useState(false);
  const comment = data.comments[index];
  const [editedContent, setEditedContent] = useState(comment? comment.content : "");

  useEffect(() => {
    if (comment) {
      setEditedContent(comment.content);
    }
  }, [comment]);

  if (!comment) {
    return (
        <div style={{...style, textAlign: "center", padding: "10px"}}>
          Loading...
        </div>
    );
  }

  const handleEditClick = () => {
    setIsEditing(true);
  };

  const handleCancelEdit = () => {
    setEditedContent(comment.content);
    setIsEditing(false);
  };

  const handleSaveEdit = () => {
    data.onEdit(comment.commentId, editedContent);
    setIsEditing(false);
  };

  return (
      <div
          style={{
            ...style,
            padding: "8px",
            borderBottom: "1px solid #f0f0f0",
          }}
      >
        <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between"
            }}
        >
          <div style={{display: "flex", alignItems: "center"}}>
            <Avatar
                src={`http://localhost:8080${comment.commentAuthorProfileImageUrl}`}
                size={32}
                style={{marginRight: "8px"}}
            />
            <Typography.Text strong>{comment.commentAuthorName}</Typography.Text>
          </div>
          <div>
            {!isEditing && (
                <Button
                    type="link"
                    icon={<EditOutlined/>}
                    onClick={handleEditClick}
                />
            )}
            <Button
                type="link"
                icon={<DeleteOutlined/>}
                onClick={() => data.onDelete(comment.commentId)}
            />
          </div>
        </div>
        <div style={{marginTop: "4px"}}>
          {isEditing ? (
              <Input.TextArea
                  value={editedContent}
                  onChange={(e) => setEditedContent(e.target.value)}
                  autoSize={{minRows: 2, maxRows: 4}}
              />
          ) : (
              <Typography.Text>{comment.content}</Typography.Text>
          )}
        </div>
        <div
            style={{
              marginTop: "4px",
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
            }}
        >
          <Typography.Text type="secondary" style={{fontSize: 12}}>
            {moment(comment.createdAt).format("YY년 M월 D일 H시 m분")}
          </Typography.Text>
          {isEditing && (
              <div>
                <Button
                    type="link"
                    icon={<CheckOutlined/>}
                    onClick={handleSaveEdit}
                />
                <Button
                    type="link"
                    icon={<CloseOutlined/>}
                    onClick={handleCancelEdit}
                />
              </div>
          )}
        </div>
      </div>
  );
};

const CommentList: React.FC<CommentListProps> = ({
                                                   todoId,
                                                   comments,
                                                   loadMore,
                                                   hasMore,
                                                   onDelete,
                                                   onEdit
                                                 }) => {

  const listRef = useRef<ListWindow>(null);
  const getItemSize = useCallback((index: number) => {
    const baseHeight = 100;
    const comment = comments[index];
    if (comment) {
      const extraLines = Math.ceil(comment.content.length / 50);
      return baseHeight + extraLines * 20;
    }
    return baseHeight;
  }, [comments]);

  const itemCount = hasMore ? comments.length + 1 : comments.length;
  const isItemLoaded = (index: number) => !hasMore || index < comments.length;
  const itemData: CommentRowData = {todoId, comments, onDelete, onEdit};

  return (
      <div style={{height: 300, overflowY: "auto", width: "100%"}}>
        <InfiniteLoader isItemLoaded={isItemLoaded} loadMoreItems={loadMore} itemCount={itemCount}>
          {({onItemsRendered}) => (
              <ListWindow
                  height={300}
                  itemCount={itemCount}
                  itemSize={getItemSize}
                  width="100%"
                  onItemsRendered={onItemsRendered}
                  ref={listRef}
                  itemData={itemData}
              >
                {CommentRow}
              </ListWindow>
          )}
        </InfiniteLoader>
      </div>
  );
};

export default CommentList;