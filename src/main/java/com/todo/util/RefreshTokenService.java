package com.todo.util;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final StringRedisTemplate redisTemplate;

  private static final long REFRESH_TOKEN_EXPIRE = 60 * 60 * 24 * 7;

  public void saveRefreshToken(String email, String refreshToken) {
    redisTemplate.opsForValue().set(email, refreshToken, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);
  }

  public String getRefreshToken(String email) {
    return redisTemplate.opsForValue().get(email);
  }

  public void deleteRefreshToken(String email) {
    redisTemplate.delete(email);
  }
}