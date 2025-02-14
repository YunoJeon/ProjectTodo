package com.todo.notification.controller;

import com.todo.notification.dto.NotificationDto;
import com.todo.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Notification API", description = "알림 API")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  @Operation(summary = "알림 목록 조회 API", description = "알림 목록을 조회할 수 있습니다. 읽음처리 된 알림은 조회되지 않습니다. 페이징 처리 되어있습니다.")
  public ResponseEntity<Page<NotificationDto>> getNotifications(Authentication auth,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    return ResponseEntity.ok(notificationService.getNotifications(auth, page, pageSize));
  }

  @PutMapping("/{notificationId}")
  @Operation(summary = "알림 읽음처리 API", description = "읽음처리 할 수 있습니다. 페이징 처리 되어있습니다.")
  public ResponseEntity<Page<NotificationDto>> updateNotification(Authentication auth,
      @PathVariable Long notificationId,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize) {

    return ResponseEntity.ok(
        notificationService.updateNotifications(auth, notificationId, page, pageSize));
  }
}