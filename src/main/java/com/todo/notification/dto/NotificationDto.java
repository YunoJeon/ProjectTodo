package com.todo.notification.dto;

import com.todo.notification.entity.Notification;
import lombok.Builder;

@Builder
public record NotificationDto(

    Long notificationId,
    String message,
    boolean isInvitation,
    Long projectId
) {

  public static NotificationDto fromEntity(Notification notification) {

    return NotificationDto.builder()
        .notificationId(notification.getId())
        .message(notification.getMessage())
        .isInvitation(notification.isInvitation())
        .projectId(notification.getProjectId())
        .build();
  }
}