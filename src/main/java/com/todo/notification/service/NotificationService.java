package com.todo.notification.service;

import static com.todo.exception.ErrorCode.ALREADY_EXISTS_READ;
import static com.todo.exception.ErrorCode.FORBIDDEN;
import static com.todo.exception.ErrorCode.NOTIFICATION_NOT_FOUND;

import com.todo.exception.CustomException;
import com.todo.notification.dto.NotificationDto;
import com.todo.notification.entity.Notification;
import com.todo.notification.repository.NotificationRepository;
import com.todo.project.entity.Project;
import com.todo.todo.entity.Todo;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

  private final NotificationRepository notificationRepository;

  private final UserQueryService userQueryService;

  private final NotificationQueryService notificationQueryService;

  @Transactional
  public void saveNotification(User user, String message, boolean isInvitation, Long projectId) {

    notificationRepository.save(Notification.of(user, message, isInvitation, projectId));
  }

  public void saveNotificationMultiple(List<User> users, String message) {

    for (User user : users) {
      saveNotification(user, message, false, null);
    }
  }

  public void sendInvitationNotification(User owner, User invitedUser, Project project) {

    String message = String.format("\"%s\" 님이 \"%s\" 프로젝트에 초대하셨습니다. 참여하시겠습니까?",
        owner.getName(), project.getName());

    saveNotification(invitedUser, message, true, project.getId());
  }

  public void notifyUserOnJoin(List<User> users, User invitedUser, Project project) {

    String message = String.format("\"%s\" 님이 \"%s\" 프로젝트에 참여하였습니다.",
        invitedUser.getName(), project.getName());

    saveNotificationMultiple(users, message);
  }

  public void notifyProjectUserRemoval(List<User> users, User deletedUser, Project project) {

    String message = String.format("\"%s\" 님이 \"%s\" 프로젝트에서 제외되었습니다.",
        deletedUser.getName(), project.getName());

    saveNotificationMultiple(users, message);
  }

  public void notifyTodoAddedByOthers(List<User> users, Todo todo, Project project) {

    String message = String.format("\"%s\" 프로젝트에 \"%s\" Todo 가 생성되었습니다.",
        project.getName(), todo.getTitle());

    saveNotificationMultiple(users, message);
  }

  public void notifyTodoStatusChangedByOthers(List<User> users, Todo todo, Project project) {

    String message = String.format("\"%s\" 프로젝트의 \"%s\" Todo 의 상태가 변경되었습니다.",
        project.getName(), todo.getTitle());

    saveNotificationMultiple(users, message);
  }

  public void notifyCommentAddedByOthers(List<User> users, Todo todo, Project project) {

    String message = String.format("\"%s\" 프로젝트의 \"%s\" Todo 에 댓글이 추가되었습니다.",
        project.getName(), todo.getTitle());

    saveNotificationMultiple(users, message);
  }

  public Page<NotificationDto> getNotifications(Authentication auth, int page, int pageSize) {

    User user = userQueryService.findByEmail(auth.getName());

    return paginateResult(page, pageSize, user);
  }

  @Transactional
  public Page<NotificationDto> updateNotifications(Authentication auth, Long notificationId,
      int page, int pageSize) {

    User user = userQueryService.findByEmail(auth.getName());

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND));

    if (!notification.getUser().getId().equals(user.getId())) {

      throw new CustomException(FORBIDDEN);
    }

    if (notification.isRead()) {

      throw new CustomException(ALREADY_EXISTS_READ);
    }

    notification.update();

    return paginateResult(page, pageSize, user);
  }

  private Page<NotificationDto> paginateResult(int page, int pageSize, User user) {
    Pageable pageable = PageRequest.of(page - 1, pageSize);

    Page<Notification> notifications = notificationQueryService.findByUserIsReadFalse(user, pageable);

    List<NotificationDto> dtoList = notifications.getContent().stream().map(
        NotificationDto::fromEntity).toList();

    return new PageImpl<>(dtoList, pageable, notifications.getTotalElements());
  }
}