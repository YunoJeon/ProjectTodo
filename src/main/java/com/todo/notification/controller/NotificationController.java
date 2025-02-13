package com.todo.notification.controller;

import com.todo.notification.dto.NotificationDto;
import com.todo.notification.service.NotificationService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<Page<NotificationDto>> getNotifications(Authentication auth,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    return ResponseEntity.ok(notificationService.getNotifications(auth, page, pageSize));
  }

  @PutMapping("/{notificationId}")
  public ResponseEntity<Page<NotificationDto>> updateNotification(Authentication auth,
      @PathVariable Long notificationId,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    return ResponseEntity.ok(
        notificationService.updateNotifications(auth, notificationId, page, pageSize));
  }
}