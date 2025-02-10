package com.todo.security.jwt;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

  private final StringRedisTemplate redisTemplate;

  @Value("${jwt.refresh.expiration}")
  private long refreshTokenExpire;

  public void saveRefreshToken(String email, String refreshToken) {
    redisTemplate.opsForValue().set(email, refreshToken, refreshTokenExpire, TimeUnit.SECONDS);
  }

  public String getRefreshToken(String email) {
    return redisTemplate.opsForValue().get(email);
  }

  public void deleteRefreshToken(String email) {
    Boolean result = redisTemplate.delete(email);

    log.info("delete refresh token for {}: result = {}", email, result);
  }
}