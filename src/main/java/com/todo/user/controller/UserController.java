package com.todo.user.controller;

import com.todo.user.dto.ChangePasswordDto;
import com.todo.user.dto.UserResponseDto;
import com.todo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User API", description = "회원 관련 API")
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  @Operation(summary = "내 정보 조회 API", description = "내 정보를 조회할 수 있습니다.")
  public ResponseEntity<UserResponseDto> getMyInfo(Authentication auth) {

    return ResponseEntity.ok(userService.getMyInfo(auth));
  }

  @PutMapping("/me/reset-password")
  @Operation(summary = "비밀번호 변경 API", description = "비밀번호를 변경할 수 있습니다.")
  public ResponseEntity<UserResponseDto> changePassword(Authentication auth,
      @RequestBody @Valid ChangePasswordDto changePasswordDto) {

    return ResponseEntity.ok(userService.changePassword(auth, changePasswordDto));
  }

  @GetMapping("/info/{email}")
  @Operation(summary = "회원 조회 API", description = "특정 회원의 정보를 조회할 수 있습니다.")
  public ResponseEntity<UserResponseDto> getUser(@PathVariable String email) {

    return ResponseEntity.ok(userService.getUser(email));
  }

  @PostMapping("/me/profile")
  @Operation(summary = "프로필 이미지 수정 API", description = "내 프로필 이미지를 수정할 수 있습니다.")
  public ResponseEntity<Map<String, String>> updateProfileImage(
      Authentication auth,
      @RequestParam("image") MultipartFile image) {

    return ResponseEntity.ok(
        Map.of("profileImageUrl", userService.updateProfileImage(auth, image)));
  }
}