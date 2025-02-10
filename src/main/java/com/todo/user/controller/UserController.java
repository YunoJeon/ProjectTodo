package com.todo.user.controller;

import com.todo.user.dto.ChangePasswordDto;
import com.todo.user.dto.UserResponseDto;
import com.todo.user.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getMyInfo(Authentication auth) {

    return ResponseEntity.ok(userService.getMyInfo(auth));
  }

  @PutMapping("/me/reset-password")
  public ResponseEntity<UserResponseDto> changePassword(Authentication auth,
      @RequestBody @Valid ChangePasswordDto changePasswordDto) {

    return ResponseEntity.ok(userService.changePassword(auth, changePasswordDto));
  }

  @GetMapping("/info/{userId}")
  public ResponseEntity<UserResponseDto> getUser(@PathVariable Long userId) {

    return ResponseEntity.ok(userService.getUser(userId));
  }

  @PostMapping("/me/profile")
  public ResponseEntity<Map<String, String>> updateProfileImage(
      Authentication auth,
      @RequestParam("image") MultipartFile image) {

    return ResponseEntity.ok(
        Map.of("profileImageUrl", userService.updateProfileImage(auth, image)));
  }
}