import React, {useEffect, useState} from "react";
import api from "../services/api";
import {Button, message, Modal, Spin, Typography} from "antd";
import moment from "moment";
import InfiniteLoader from "react-window-infinite-loader";
import {FixedSizeList as List} from "react-window";

interface ActivityLogResponseDto {
  activityLogId: number;
  actionDetail: string;
  changerName: string;
  actionType: string;
  todoId: number;
  todoTitle: string;
  todoVersion: number;
  snapshotId: number;
  createdAt: string;
}

interface ActivityLogModalProps {
  projectId: number;
  visible: boolean;
  onClose: () => void;
}

const ActivityLogModal: React.FC<ActivityLogModalProps> = ({
                                                             projectId, visible, onClose
                                                           }) => {
  const [logs, setLogs] = useState<ActivityLogResponseDto[]>([]);
  const [loading, setLoading] = useState(false);
  const pageSize = 10;
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);

  const handleRestore = async (todoId: number, snapshotId: number) => {
    try {
      await api.post(`/todos/${todoId}/restore/${snapshotId}`);
      message.success("할일이 복구되었습니다.");
      window.location.reload();
    } catch (error) {
      console.error("할일 복구 실패", error);
      message.error("할일 복구에 실패하였습니다.");
    }
  }

  const fetchActivityLogs = async (currentPage: number) => {
    setLoading(true);
    try {
      const response = await api.get(`/projects/${projectId}/logs`, {
        params: {
          page: currentPage,
          pageSize
        }
      });
      const newLogs: ActivityLogResponseDto[] = response.data.content || [];

      if (newLogs.length < pageSize) {
        setHasMore(false);
      }

      if (currentPage === 1) {
        setLogs(newLogs);
      } else {
        setLogs((prev) => [...prev, ...newLogs]);
      }

      setPage(currentPage + 1);
    } catch (error) {
      console.error("활동 로그 조회 실패", error);
      message.error("활동 로그를 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (visible) {
      setLogs([]);
      setPage(1);
      setHasMore(true);
      fetchActivityLogs(1);
    }
  }, [projectId, visible]);

  const isItemLoaded = (index: number) => !hasMore || index < logs.length;

  const loadMoreItems = () => {
    if (!loading && hasMore) {
      return fetchActivityLogs(page);
    }
    return Promise.resolve();
  }

  const Row = ({index, style}: { index: number; style: React.CSSProperties }) => {
    const log = logs[index];

    if (!log) {
      return (
          <div style={{...style, textAlign: "center", padding: "10px"}}>
            Loading...
          </div>
      );
    }
    return (
        <div style={{...style, padding: "8px", borderBottom: "1px solid #f0f0f0"}}>
          <div style={{display: "block"}}>
                      <span
                          style={{
                            marginRight: "8px",
                            fontSize: "15px",
                            fontWeight: "bold",
                            color: log.actionType === "TODO" ? "#2e7d32" : "#1565c0",
                            background: log.actionType === "TODO" ? "#c8e6c9" : "#bbdefb",
                            padding: "4px 8px",
                            borderRadius: "20px",
                            display: "inline-block",
                            marginBottom: "4px"
                          }}
                      >
                        {log.actionType === "TODO" ? "📝 Todo" : "📁 Project"}
                      </span>
            {log.actionType === "TODO" && (
                <Button
                    type="primary"
                    onClick={() => handleRestore(log.todoId, log.snapshotId)}
                >
                  복구
                </Button>
            )}
            <Typography.Paragraph style={{marginTop: "4px"}}>
              {log.actionDetail}
            </Typography.Paragraph>

            {log.actionType === "TODO" && (
                <>
                  <Typography.Paragraph type="secondary" style={{marginBottom: "4px"}}>
                    - [{log.todoTitle}]
                  </Typography.Paragraph>
                  <Typography.Paragraph type="secondary" style={{marginBottom: "4px"}}>
                    - 버전: {log.todoVersion}
                  </Typography.Paragraph>
                </>
            )}
            <Typography.Paragraph type="secondary" style={{marginBottom: "4px"}}>
              - 변경자: {log.changerName}
            </Typography.Paragraph>
            <Typography.Paragraph type="secondary" style={{marginBottom: "4px"}}>
              - 변경일: {moment(log.createdAt).format("YY년 M월 D일 H시 m분")}
            </Typography.Paragraph>
          </div>
        </div>
    );
  };

  const itemCount = hasMore ? logs.length + 1 : logs.length;

  return (
      <Modal
          open={visible}
          title="활동 로그"
          onCancel={onClose}
          footer={null}
          width={400}
      >
        {loading && page === 2 && logs.length === 0 ? (
            <Spin style={{display: "block", textAlign: "center", marginTop: "2rem"}}/>
        ) : (
            <InfiniteLoader
                isItemLoaded={isItemLoaded}
                loadMoreItems={loadMoreItems}
                itemCount={itemCount}
            >
              {({onItemsRendered, ref}) => (
                  <List
                      height={400}
                      itemCount={itemCount}
                      itemSize={200}
                      width="100%"
                      onItemsRendered={onItemsRendered}
                      ref={ref}
                  >
                    {Row}
                  </List>
              )}
            </InfiniteLoader>
        )}
      </Modal>
  );
};

export default ActivityLogModal;