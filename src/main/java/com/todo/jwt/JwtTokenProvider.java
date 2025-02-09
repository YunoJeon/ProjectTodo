package com.todo.jwt;

import static com.todo.exception.ErrorCode.INVALID_TOKEN;

import com.todo.exception.CustomException;
import com.todo.util.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

  private final RefreshTokenService refreshTokenService;

  private final Key secretKey;

  private final long accessTokenValidity;

  private final long refreshTokenValidity;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access.expiration}") long accessTokenValidity,
      @Value("${jwt.refresh.expiration}") long refreshTokenValidity,
      RefreshTokenService refreshTokenService) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenValidity = accessTokenValidity;
    this.refreshTokenValidity = refreshTokenValidity;
    this.refreshTokenService = refreshTokenService;
  }

  public String createAccessToken(String email) {
    return createToken(email, accessTokenValidity);
  }

  public String createRefreshToken(String email) {

    String refreshToken = createToken(email, refreshTokenValidity);
    refreshTokenService.saveRefreshToken(email, refreshToken);

    return refreshToken;
  }

  private String createToken(String email, long validity) {

    Date now = new Date();
    Date expiration = new Date(now.getTime() + validity);

    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(now)
        .setExpiration(expiration)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("Expired JWT token: {}", e.getMessage());
      throw new CustomException(INVALID_TOKEN);
    } catch (JwtException | IllegalArgumentException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
      return false;
    }
  }

  public String getEmail(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateRefreshToken(String email, String refreshToken) {

    if (!validateToken(refreshToken)) {
      return false;
    }

    String storedToken = refreshTokenService.getRefreshToken(email);
    return storedToken != null && storedToken.equals(refreshToken) && validateToken(refreshToken);
  }
}