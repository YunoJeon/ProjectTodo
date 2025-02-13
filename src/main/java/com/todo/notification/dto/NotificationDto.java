package com.todo.notification.dto;

import com.todo.notification.entity.Notification;

public record NotificationDto(

    Long notificationId,
    String message
) {

  public static NotificationDto fromEntity(Notification notification) {

    return new NotificationDto(notification.getId(), notification.getMessage());
  }
}