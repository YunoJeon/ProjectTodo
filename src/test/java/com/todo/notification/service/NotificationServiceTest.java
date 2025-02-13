package com.todo.notification.service;

import static com.todo.exception.ErrorCode.ALREADY_EXISTS_READ;
import static com.todo.exception.ErrorCode.FORBIDDEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todo.exception.CustomException;
import com.todo.notification.dto.NotificationDto;
import com.todo.notification.entity.Notification;
import com.todo.notification.repository.NotificationRepository;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private UserQueryService userQueryService;
  @Mock
  private NotificationQueryService notificationQueryService;

  @InjectMocks
  private NotificationService notificationService;

  private Authentication auth;
  private User testUser;
  private Notification notification;

  @BeforeEach
  void setUp() {

    auth = new UsernamePasswordAuthenticationToken("test@test.com", null);

    testUser = User.builder()
        .id(1L)
        .email("test@test.com")
        .build();

    notification = Notification.builder()
        .id(10L)
        .user(testUser)
        .isRead(false)
        .message("테스트용")
        .build();
  }

  @Test
  @DisplayName("알림 저장이 성공한다")
  void save_notification_success() {
    // given
    // when
    notificationService.saveNotification(testUser, "테스트");
    // then
    verify(notificationRepository).save(any(Notification.class));
  }

  @Test
  @DisplayName("여러명의 알림 저장이 성공한다")
  void save_notification_multiple_success() {
    // given
    User testUser2 = User.builder()
        .id(2L)
        .build();
    List<User> users = List.of(testUser, testUser2);
    // when
    notificationService.saveNotificationMultiple(users, "테스트");
    // then
    verify(notificationRepository, times(users.size())).save(any(Notification.class));
  }

  @Test
  @DisplayName("알림 목록 조회가 성공한다")
  void get_notifications_success() {
    // given
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);

    List<Notification> notifications = Collections.singletonList(
        Notification.of(testUser, "테스트 메세지"));

    Page<Notification> dtoPage = new PageImpl<>(notifications, PageRequest.of(0, 10),
        notifications.size());

    when(notificationQueryService.findByUserIsReadFalse(testUser, PageRequest.of(0, 10)))
        .thenReturn(dtoPage);
    // when
    Page<NotificationDto> result = notificationService.getNotifications(auth, 1, 10);
    // then
    assertEquals(1, result.getTotalElements());
    assertEquals("테스트 메세지", result.getContent().get(0).message());
  }

  @Test
  @DisplayName("알림 읽음처리에 성공한다")
  void update_notification_success() {
    // given
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(notificationRepository.findById(notification.getId()))
        .thenReturn(Optional.of(notification));

    List<Notification> notifications = Collections.singletonList(notification);
    Page<Notification> dtoPage = new PageImpl<>(notifications, PageRequest.of(0, 10),
        notifications.size());
    when(notificationQueryService.findByUserIsReadFalse(testUser, PageRequest.of(0, 10)))
        .thenReturn(dtoPage);
    // when
    notificationService.updateNotifications(auth, notification.getId(), 1, 10);
    // then
    assertTrue(notification.isRead());
  }

  @Test
  @DisplayName("알림 소유자가 아니면 읽음처리에 실패한다")
  void update_notification_failure_forbidden() {
    // given
    User testUser2 = User.builder()
        .id(2L)
        .email("test2@test.com")
        .build();

    Notification notification2 = Notification.builder()
        .id(20L)
        .user(testUser2)
        .build();

    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(notificationRepository.findById(notification2.getId()))
        .thenReturn(Optional.of(notification2));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> notificationService.updateNotifications(auth, notification2.getId(), 1, 10));
    // then
    assertEquals(e.getErrorCode(), FORBIDDEN);
  }

  @Test
  @DisplayName("이미 읽은 알림인 경우 읽음처리에 실패한다")
  void update_notification_failure_conflict() {
    // given
    Notification notification2 = Notification.builder()
        .id(20L)
        .user(testUser)
        .isRead(true)
        .build();

    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(notificationRepository.findById(notification2.getId()))
        .thenReturn(Optional.of(notification2));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> notificationService.updateNotifications(auth, notification2.getId(), 1, 10));
    // then
    assertEquals(e.getErrorCode(), ALREADY_EXISTS_READ);
  }
}