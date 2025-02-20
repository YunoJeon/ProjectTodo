package com.todo.user.service;

import static com.todo.exception.ErrorCode.REQUEST_VALIDATION_FAIL;

import com.todo.exception.CustomException;
import com.todo.image.service.ImageService;
import com.todo.user.dto.ChangePasswordDto;
import com.todo.user.dto.UserResponseDto;
import com.todo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private final UserQueryService userQueryService;

  private final PasswordEncoder passwordEncoder;

  private final ImageService imageService;

  public UserResponseDto getMyInfo(Authentication auth) {

    return UserResponseDto.fromEntity(userQueryService.findByEmail(auth.getName()));
  }

  @Transactional
  public UserResponseDto changePassword(Authentication auth, ChangePasswordDto changePasswordDto) {

    User user = userQueryService.findByEmail(auth.getName());

    if (!passwordEncoder.matches(changePasswordDto.confirmPassword(), user.getPassword())) {
      throw new CustomException(REQUEST_VALIDATION_FAIL, "현재 비밀번호가 올바르지 않습니다.");
    }

    user.updatePassword(passwordEncoder.encode(changePasswordDto.password()));

    return UserResponseDto.fromEntity(user);
  }

  public UserResponseDto getUser(String email) {

    return UserResponseDto.fromEntity(userQueryService.findByEmail(email));
  }

  @Transactional
  public String updateProfileImage(Authentication auth, MultipartFile image) {

    User user = userQueryService.findByEmail(auth.getName());

    String newImageUrl = imageService.updateProfileImage(user.getProfileImageUrl(), image);

    user.updateProfileImage(newImageUrl);

    return newImageUrl;
  }
}