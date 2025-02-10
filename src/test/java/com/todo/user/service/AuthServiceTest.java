package com.todo.user.service;

import static com.todo.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.todo.exception.ErrorCode.INVALID_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.userdetails.User.withUsername;

import com.todo.exception.CustomException;
import com.todo.security.authentication.CustomAuthenticationProvider;
import com.todo.security.jwt.JwtTokenProvider;
import com.todo.security.jwt.RefreshTokenService;
import com.todo.user.dto.AuthDto;
import com.todo.user.dto.AuthLoginDto;
import com.todo.user.entity.User;
import com.todo.user.repository.UserRepository;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserQueryService userQueryService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private CustomAuthenticationProvider authenticationProvider;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private RefreshTokenService refreshTokenService;

  @InjectMocks
  private AuthService authService;

  private AuthDto user;
  private AuthLoginDto login;
  private User testUser;

  @BeforeEach
  void setUp() {

    user = new AuthDto(
        "email@email.com",
        "password111",
        "010-1234-5678",
        "테스트",
        null);

    login = new AuthLoginDto(
        "email@email.com",
        "password111");

    testUser = User.builder()
        .id(1L)
        .email("email@email.com")
        .password("password111")
        .phone("010-1234-5678")
        .name("테스트")
        .profileImageUrl(null)
        .build();
  }

  @Test
  @DisplayName("메일 중복확인에 성공한다 (false 반환)")
  void check_email_success() {
    // given
    String email = "email@email.com";
    when(userRepository.existsByEmail(email)).thenReturn(false);
    // when
    boolean exists = authService.emailExists(email);
    // then
    assertFalse(exists);
  }

  @Test
  @DisplayName("이미 존재하는 메일인 경우 중복확인에 실패한다 (true 반환)")
  void check_email_failure_already_exist() {
    String email = "email@email.com";
    when(userRepository.existsByEmail(email)).thenReturn(true);
    // when
    boolean exists = authService.emailExists(email);
    // then
    assertTrue(exists);
  }

  @Test
  @DisplayName("회원가입에 성공한다")
  void create_user_success() {
    // given
    when(userRepository.existsByEmail(user.email())).thenReturn(false);
    // when
    authService.createUser(user);
    // then
    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("이미 존재하는 메일인 경우 회원가입에 실패한다")
  void create_user_failure_already_exist() {
    // given
    when(userRepository.existsByEmail(user.email())).thenReturn(true);
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> authService.createUser(user));
    // then
    assertEquals(e.getErrorCode(), ALREADY_EXISTS_EMAIL);
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("로그인에 성공하면 Access, Refresh Token 을 발급받는다")
  void sign_in_success() {
    // given
    UserDetails userDetails = withUsername(login.email()).password(
        login.password()).authorities(Collections.emptyList()).build();
    Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());
    when(authenticationProvider.authenticate(
        any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
    when(userQueryService.findByEmail(login.email())).thenReturn(testUser);
    when(jwtTokenProvider.createAccessToken(login.email())).thenReturn("accessToken");
    when(jwtTokenProvider.createRefreshToken(login.email())).thenReturn("refreshToken");
    // when
    Map<String, String> tokens = authService.signIn(login);
    // then
    assertNotNull(tokens);
    assertThat(tokens).containsKeys("accessToken", "refreshToken");
    assertEquals("accessToken", tokens.get("accessToken"));
    assertEquals("refreshToken", tokens.get("refreshToken"));
  }

  @Test
  @DisplayName("유효한 Refresh Token 으로 Access Token 을 재발급 받는다")
  void refresh_access_token_success() {
    // given
    String validRefreshToken = "refreshToken";
    when(jwtTokenProvider.getEmail(validRefreshToken)).thenReturn(testUser.getEmail());
    when(userQueryService.findByEmail(login.email())).thenReturn(testUser);
    when(jwtTokenProvider.validateRefreshToken(testUser.getEmail(), validRefreshToken)).thenReturn(
        true);
    when(jwtTokenProvider.createAccessToken(testUser.getEmail())).thenReturn("newAccessToken");
    // when
    Map<String, String> tokens = authService.refreshAccessToken(validRefreshToken);
    // then
    assertNotNull(tokens);
    assertTrue(tokens.containsKey("accessToken"));
    assertEquals("newAccessToken", tokens.get("accessToken"));
  }

  @Test
  @DisplayName("Refresh Token 이 null 인 경우 Access Token 재발급에 실패한다")
  void refresh_access_token_failure_null_token() {
    // given
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> authService.refreshAccessToken(null));
    // then
    assertEquals(e.getErrorCode(), INVALID_TOKEN);
  }

  @Test
  @DisplayName("로그아웃 시 Refresh Token 이 삭제된다")
  void sign_out_success() {
    // given
    when(userQueryService.findByEmail(login.email())).thenReturn(testUser);
    doNothing().when(refreshTokenService).deleteRefreshToken(user.email());
    // when
    authService.signOut(user.email());
    // then
    verify(userQueryService).findByEmail(user.email());
    verify(refreshTokenService).deleteRefreshToken(user.email());
  }
}