package com.todo.user.controller;

import com.todo.security.jwt.JwtTokenProvider;
import com.todo.user.dto.AuthDto;
import com.todo.user.dto.AuthLoginDto;
import com.todo.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "인증 및 인가 관련 API")
public class AuthController {

  private final AuthService authService;

  private final JwtTokenProvider jwtTokenProvider;

  @Value("${jwt.refresh.expiration}")
  private int refreshTokenExpire;

  @GetMapping("/check-email")
  @Operation(summary = "중복 이메일 체크 API", description = "회원가입 전 이메일 중복을 진행할 수 있습니다.")
  public ResponseEntity<Boolean> checkEmailExists(
      @RequestParam @NotBlank @Email(message = "이메일 형식이 아닙니다.") String email) {

    return ResponseEntity.ok(authService.emailExists(email));
  }

  @PostMapping("/sign-up")
  @Operation(summary = "회원가입 API", description = "회원가입을 진행할 수 있습니다.")
  public ResponseEntity<Void> signUp(@RequestBody @Valid AuthDto authDto) {

    authService.createUser(authDto);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/sign-in")
  @Operation(summary = "로그인 API", description = "로그인을 진행할 수 있습니다. 로그인 시 access token 이 발급되고, refresh token 은 cookie 에 저장됩니다.")
  public ResponseEntity<Map<String, String>> signIn(@RequestBody @Valid AuthLoginDto authLoginDto,
      HttpServletResponse response) {

    Map<String, String> tokens = authService.signIn(authLoginDto);

    Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(refreshTokenExpire);
    response.addCookie(refreshTokenCookie);

    return ResponseEntity.ok(tokens);
  }

  @PostMapping("/refresh")
  @Operation(summary = "토큰 재발급 API", description = "http only cookie 에 저장된 refresh token 정보와 서버 에 저장된 정보를 비교하여 access token 을 재발급 합니다.")
  public ResponseEntity<Map<String, String>> refreshAccessToken(
      @CookieValue(value = "refreshToken", required = false) String refreshToken) {

    return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
  }

  @PostMapping("/sign-out")
  @Operation(summary = "로그아웃 API", description = "로그아웃을 진행할 수 있습니다. 로그아웃 시 cookie 에 세팅된 refresh token 은 무효화 됩니다.")
  public ResponseEntity<Void> signOut(
      @CookieValue(value = "refreshToken", required = false) String refreshToken,
      HttpServletResponse response) {

    if (refreshToken != null) {
      String email = jwtTokenProvider.getEmail(refreshToken);

      authService.signOut(email);

      Cookie refreshTokenCookie = new Cookie("refreshToken", null);
      refreshTokenCookie.setHttpOnly(true);
      refreshTokenCookie.setSecure(true);
      refreshTokenCookie.setPath("/");
      refreshTokenCookie.setMaxAge(0);
      response.addCookie(refreshTokenCookie);
    }

    SecurityContextHolder.clearContext();

    return ResponseEntity.ok().build();
  }
}