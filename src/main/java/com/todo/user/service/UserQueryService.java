package com.todo.user.service;

import static com.todo.exception.ErrorCode.USER_NOT_FOUND;

import com.todo.exception.CustomException;
import com.todo.user.entity.User;
import com.todo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

  private final UserRepository userRepository;

  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }
}