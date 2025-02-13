package com.todo.user.controller;

import com.todo.security.jwt.JwtTokenProvider;
import com.todo.user.dto.AuthDto;
import com.todo.user.dto.AuthLoginDto;
import com.todo.user.service.AuthService;
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
public class AuthController {

  private final AuthService authService;

  private final JwtTokenProvider jwtTokenProvider;

  @Value("${jwt.refresh.expiration}")
  private int refreshTokenExpire;

  @GetMapping("/check-email")
  public ResponseEntity<Boolean> checkEmailExists(
      @RequestParam @NotBlank @Email(message = "이메일 형식이 아닙니다.") String email) {

    return ResponseEntity.ok(authService.emailExists(email));
  }

  @PostMapping("/sign-up")
  public ResponseEntity<Void> signUp(@RequestBody @Valid AuthDto authDto) {

    authService.createUser(authDto);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/sign-in")
  public ResponseEntity<Map<String, String>> signIn(@RequestBody @Valid AuthLoginDto authLoginDto,
      HttpServletResponse response) {

    Map<String, String> tokens = authService.signIn(authLoginDto);

    Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setPath("/api/auth/refresh");
    refreshTokenCookie.setMaxAge(refreshTokenExpire);
    response.addCookie(refreshTokenCookie);

    return ResponseEntity.ok(tokens);
  }

  @PostMapping("/refresh")
  public ResponseEntity<Map<String, String>> refreshAccessToken(
      @CookieValue(value = "refreshToken", required = false) String refreshToken) {

    return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
  }

  @PostMapping("/sign-out")
  public ResponseEntity<Void> signOut(
      @CookieValue(value = "refreshToken", required = false) String refreshToken,
      HttpServletResponse response) {

    if (refreshToken != null) {
      String email = jwtTokenProvider.getEmail(refreshToken);

      authService.signOut(email);

      Cookie refreshTokenCookie = new Cookie("refreshToken", null);
      refreshTokenCookie.setHttpOnly(true);
      refreshTokenCookie.setSecure(true);
      refreshTokenCookie.setPath("/api/auth/sign-out");
      refreshTokenCookie.setMaxAge(0);
      response.addCookie(refreshTokenCookie);
    }

    SecurityContextHolder.clearContext();

    return ResponseEntity.ok().build();
  }
}