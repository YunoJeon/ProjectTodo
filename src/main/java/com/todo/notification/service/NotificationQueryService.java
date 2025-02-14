package com.todo.notification.service;

import com.todo.notification.entity.Notification;
import com.todo.notification.repository.NotificationRepository;
import com.todo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService {

  private final NotificationRepository notificationRepository;

  public Page<Notification> findByUserIsReadFalse(User user, Pageable pageable) {

    return notificationRepository.findByUserAndReadFalse(user, pageable);
  }
}