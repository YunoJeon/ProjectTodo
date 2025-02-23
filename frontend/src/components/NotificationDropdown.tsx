import React, {useEffect, useState} from "react";
import api from "../services/api";
import {Button, message} from "antd";
import {FixedSizeList as ListWindow, ListChildComponentProps} from "react-window";
import InfiniteLoader from "react-window-infinite-loader";
import {DeleteOutlined} from "@ant-design/icons";

interface NotificationDto {
  notificationId: number;
  message: string;
  isInvitation: boolean;
  projectId: number | null;
}

interface NotificationRowData {
  notifications: NotificationDto[];
  onInvitationResponse: (notification: NotificationDto, accepted: boolean) => void;
  onDelete: (notificationId: number) => void;
}

interface NotificationDropdownProps {
  reloadSignal: number;
}

const NotificationRow: React.FC<ListChildComponentProps<NotificationRowData>> = ({
                                                                                   index,
                                                                                   style,
                                                                                   data
                                                                                 }) => {
  const notification = data.notifications[index];
  if (!notification) {
    return <div style={{...style, textAlign: "center", padding: "10px"}}>Loading...</div>
  }

  return (
      <div
          style={{
            ...style,
            padding: "8px",
            borderBottom: "1px solid #f0f0f0",
            display: "flex",
            flexDirection: "column",
            justifyContent: "center"
          }}
      >
        <div>{notification.message}</div>
        <div style={{marginTop: "8px"}}>
          {notification.isInvitation ? (
              <>
                <Button
                    type="link"
                    onClick={() => data.onInvitationResponse(notification, true)}
                >
                  참여
                </Button>
                <Button
                    type="link"
                    danger
                    onClick={() => data.onInvitationResponse(notification, false)}
                >
                  거절
                </Button>
              </>
          ) : (
              <Button
                  type="link"
                  icon={<DeleteOutlined/>}
                  onClick={() => data.onDelete(notification.notificationId)}
              />
          )}
        </div>
      </div>
  );
};

const NotificationDropdown: React.FC<NotificationDropdownProps> = ({reloadSignal}) => {
      const [notifications, setNotifications] = useState<NotificationDto[]>([]);
      const [page, setPage] = useState(1);
      const [hasMore, setHasMore] = useState(true);
      const [loading, setLoading] = useState(false);
      const pageSize = 10;

      useEffect(() => {
        setNotifications([]);
        setPage(1);
        setHasMore(true);
        fetchNotifications(1);
      }, [reloadSignal]);

      const fetchNotifications = async (currentPage: number) => {
        setLoading(true);

        try {
          const response = await api.get('/notifications', {params: {page: currentPage, pageSize}});

          const newNotifications: NotificationDto[] = response.data.content || [];
          if (newNotifications.length < pageSize) {
            setHasMore(false);
          }

          if (currentPage === 1) {
            setNotifications(newNotifications);
          } else {
            setNotifications((prev) => [...prev, ...newNotifications]);
          }
          setPage(currentPage + 1);
        } catch (error) {
          console.error("알림 조회 실패", error)
          message.error("알림을 불러오지 못했습니다.");
        } finally {
          setLoading(false);
        }
      };

      const isItemLoaded = (index: number) => !hasMore || index < notifications.length;

      const loadMoreItems = () => {
        if (!loading && hasMore) {
          return fetchNotifications(page);
        }
        return Promise.resolve();
      };

      const handleInvitationResponse = (notification: NotificationDto, accepted: boolean) => {
        const confirmType = accepted ? "TRUE" : "FALSE";
        if (notification.projectId) {
          if (accepted) {
            api.put(`/projects/${notification.projectId}/collaborators/confirm`, confirmType)
            .then(() => {
              message.success("프로젝트에 참여하였습니다.");
              return api.put(`/notifications/${notification.notificationId}`, null, {
                params: {page: 1, pageSize}
              });
            })
            .then(() => {
              setNotifications((prev) =>
                  prev.filter((n) => n.notificationId !== notification.notificationId)
              );
            })
            .catch((error) => {
              console.error("초대 응답 실패", error);
              message.error("초대 응답 처리에 실패하였습니다.");
            });
          } else {
            api.put(`/notifications/${notification.notificationId}`, null, {
              params: {
                page: 1,
                pageSize
              }
            })
            .then(() => {
              message.success("초대를 거절하였습니다.");
              setNotifications((prev) =>
                  prev.filter((n) => n.notificationId !== notification.notificationId));
            })
            .catch((error) => {
              console.error("알림 읽음 처리 실패", error);
              message.error("알림 읽음 처리에 실패하였습니다.");
            });
          }
        }
      };

      const handleDeletedNotification = (notificationId: number) => {
        api.put(`/notifications/${notificationId}`, null,
            {params: {page: 1, pageSize}})
        .then(() => {
          message.success("읽음처리 되었습니다.");
          setNotifications((prev) =>
              prev.filter((n) => n.notificationId !== notificationId)
          );
        })
        .catch((error) => {
          console.error("알림 읽음 처리 실패", error);
          message.error("알림 읽음 처리에 실패하였습니다.");
        });
      };

      const itemCount = hasMore ? notifications.length + 1 : notifications.length;

      const itemData: NotificationRowData = {
        notifications,
        onInvitationResponse: handleInvitationResponse,
        onDelete: handleDeletedNotification
      };

      if (!loading && notifications.length === 0) {
        return (
            <div
                style={{
                  height: "auto",
                  width: "auto",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontWeight: "bold"
                }}
            >
              ❎‍️ 모든 알림을 확인하였습니다. ❎
            </div>
        )
      }

      return (
          <div style={{height: 300, overflow: "auto"}}>
            <InfiniteLoader
                isItemLoaded={isItemLoaded}
                loadMoreItems={loadMoreItems}
                itemCount={itemCount}
            >
              {({onItemsRendered, ref}) => (
                  <ListWindow
                      height={300}
                      itemCount={itemCount}
                      itemSize={120}
                      width="100%"
                      onItemsRendered={onItemsRendered}
                      ref={ref}
                      itemData={itemData}
                  >
                    {NotificationRow}
                  </ListWindow>
              )}
            </InfiniteLoader>
          </div>
      );
    }
;

export default NotificationDropdown;