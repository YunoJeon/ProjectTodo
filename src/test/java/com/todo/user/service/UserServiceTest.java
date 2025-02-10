package com.todo.user.service;

import static com.todo.exception.ErrorCode.REQUEST_VALIDATION_FAIL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.userdetails.User.withUsername;

import com.todo.exception.CustomException;
import com.todo.image.service.ImageService;
import com.todo.user.dto.ChangePasswordDto;
import com.todo.user.dto.UserResponseDto;
import com.todo.user.entity.User;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserQueryService userQueryService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private ImageService imageService;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private Authentication auth;

  @BeforeEach
  void setUp() {

    testUser = User.builder()
        .id(1L)
        .email("email@email.com")
        .password("password111")
        .phone("010-1234-5678")
        .name("테스트")
        .profileImageUrl("/uploads/profile/image.png")
        .build();

    UserDetails userDetails = withUsername(testUser.getEmail()).password(
        testUser.getPassword()).authorities(Collections.emptyList()).build();

    auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  @Test
  @DisplayName("내 정보 조회가 성공한다.")
  void get_my_info_success() {
    // given
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    // when
    UserResponseDto responseDto = userService.getMyInfo(auth);
    // then
    assertEquals(testUser.getEmail(), responseDto.email());
  }

  @Test
  @DisplayName("특정 회원 조회에 성공한다.")
  void get_user_info_success() {
    // given
    when(userQueryService.findById(testUser.getId())).thenReturn(testUser);
    // when
    UserResponseDto responseDto = userService.getUser(testUser.getId());
    // then
    assertEquals(testUser.getEmail(), responseDto.email());
  }

  @Test
  @DisplayName("비밀번호 변경이 성공한다.")
  void change_password_success() {
    // given
    ChangePasswordDto changePasswordDto = new ChangePasswordDto("newPassword",
        testUser.getPassword());
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(passwordEncoder.matches(changePasswordDto.confirmPassword(),
        testUser.getPassword())).thenReturn(true);
    when(passwordEncoder.encode(changePasswordDto.password())).thenReturn("newEncryptedPassword");
    // when
    userService.changePassword(auth, changePasswordDto);
    // then
    assertEquals("newEncryptedPassword", testUser.getPassword());
  }

  @Test
  @DisplayName("현재 비밀번호가 틀리면 비밀번호 변경이 실패한다.")
  void change_password_failure_invalid_password() {
    // given
    ChangePasswordDto changePasswordDto = new ChangePasswordDto("newPassword",
        "wrongPassword");
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(passwordEncoder.matches(changePasswordDto.confirmPassword(),
        testUser.getPassword())).thenReturn(false);
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> userService.changePassword(auth, changePasswordDto));
    // then
    assertEquals(REQUEST_VALIDATION_FAIL, e.getErrorCode());
  }

  @Test
  @DisplayName("프로필 이미지 수정에 성공한다.")
  void update_profile_image_success() {
    // given
    String newImageUrl = "/uploads/profile/newImage.png";
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(imageService.updateProfileImage(eq(testUser.getProfileImageUrl()),
        any(MultipartFile.class))).thenReturn(newImageUrl);
    // when
    byte[] content = "dummy".getBytes(UTF_8);
    MockMultipartFile file = new MockMultipartFile("image", "dummy.png", "image/png", content);
    String returnUrl = userService.updateProfileImage(auth, file);
    // then
    assertEquals(newImageUrl, returnUrl);
    assertEquals(testUser.getProfileImageUrl(), returnUrl);
  }
}