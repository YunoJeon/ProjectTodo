package com.todo.user.dto;

import com.todo.user.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserResponseDto(

    String email,
    String phone,
    String name,
    String profileImageUrl,
    LocalDateTime createdAt
) {

  public static UserResponseDto fromEntity(User user) {

    return UserResponseDto.builder()
        .email(user.getEmail())
        .phone(user.getPhone())
        .name(user.getName())
        .profileImageUrl(user.getProfileImageUrl())
        .createdAt(user.getCreateAt())
        .build();
  }
}