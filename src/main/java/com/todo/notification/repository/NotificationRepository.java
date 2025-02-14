package com.todo.notification.repository;

import com.todo.notification.entity.Notification;
import com.todo.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Page<Notification> findByUserAndReadFalse(User user, Pageable pageable);
}