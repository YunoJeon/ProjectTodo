package com.todo.user.service;

import static com.todo.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.todo.exception.ErrorCode.INVALID_TOKEN;
import static com.todo.exception.ErrorCode.REQUEST_VALIDATION_FAIL;

import com.todo.security.authentication.CustomAuthenticationProvider;
import com.todo.exception.CustomException;
import com.todo.security.jwt.JwtTokenProvider;
import com.todo.user.dto.AuthDto;
import com.todo.user.dto.AuthLoginDto;
import com.todo.user.entity.User;
import com.todo.user.repository.UserRepository;
import com.todo.security.jwt.RefreshTokenService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;

  private final UserQueryService userQueryService;

  private final PasswordEncoder passwordEncoder;

  private final CustomAuthenticationProvider authenticationProvider;

  private final JwtTokenProvider jwtTokenProvider;

  private final RefreshTokenService refreshTokenService;

  @Transactional
  public void createUser(AuthDto authDto) {

    if (userRepository.existsByEmail(authDto.email())) {
      throw new CustomException(ALREADY_EXISTS_EMAIL);
    }

    User user = User.of(authDto, passwordEncoder.encode(authDto.password()));

    userRepository.save(user);
  }

  public boolean emailExists(String email) {

    if (email == null || email.isEmpty()) {
      throw new CustomException(REQUEST_VALIDATION_FAIL);
    }

    return userRepository.existsByEmail(email);
  }

  public Map<String, String> signIn(AuthLoginDto authLoginDto) {

    Authentication authentication = authenticationProvider.authenticate(
        new UsernamePasswordAuthenticationToken(authLoginDto.email(), authLoginDto.password()));

    String email = authentication.getName();

    userQueryService.findByEmail(email);

    return Map.of("accessToken", jwtTokenProvider.createAccessToken(email),
        "refreshToken", jwtTokenProvider.createRefreshToken(email));
  }

  public Map<String, String> refreshAccessToken(String refreshToken) {

    if (refreshToken == null) {
      throw new CustomException(INVALID_TOKEN);
    }

    String email = jwtTokenProvider.getEmail(refreshToken);

    userQueryService.findByEmail(email);

    if (!jwtTokenProvider.validateRefreshToken(email, refreshToken)) {
      throw new CustomException(INVALID_TOKEN);
    }

    return Map.of("accessToken", jwtTokenProvider.createAccessToken(email));
  }

  @Transactional
  public void signOut(String email) {

    userQueryService.findByEmail(email);

    refreshTokenService.deleteRefreshToken(email);
  }
}