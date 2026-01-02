package com.example.modules.auth.services;

import com.example.base.enums.ErrorCode;
import com.example.base.exceptions.AppException;
import com.example.modules.redis.services.RedisService;
import com.example.modules.users.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

  @Value("${jwt.access.secret}")
  private String ACCESS_SECRET;

  @Value("${jwt.refresh.secret}")
  private String REFRESH_SECRET;

  @Value("${jwt.reset-password.secret}")
  private String RESET_PASSWORD_SECRET;

  @Value("${jwt.access.expiration}")
  private Long ACCESS_EXPIRATION;

  @Value("${jwt.refresh.expiration}")
  private Long REFRESH_EXPIRATION;

  @Value("${jwt.reset-password.expiration}")
  private Long RESET_PASSWORD_EXPIRATION;

  private final RedisService redisService;
  private final ObjectMapper objectMapper;

  public boolean isTokenInvalidated(String userId, Date tokenIssuedAt) {
    String key = "user:%s:tokens:invalidated_before".formatted(userId);
    Instant invalidatedBefore = redisService.get(key, Instant.class);

    if (invalidatedBefore == null) {
      return false;
    }

    return tokenIssuedAt.toInstant().isBefore(invalidatedBefore);
  }

  public String generateAccessToken(User user) {
    Instant currentInstant = Instant.now();
    Date issuedAt = Date.from(currentInstant);
    Date expiration = Date.from(currentInstant.plusSeconds(ACCESS_EXPIRATION));

    return Jwts.builder()
      .subject(user.getId())
      .claim("role", user.getAccount().getRole())
      .issuedAt(issuedAt)
      .expiration(expiration)
      .signWith(getSecretKeyFromString(ACCESS_SECRET))
      .compact();
  }

  public String generateRefreshToken(User user) {
    Instant currentInstant = Instant.now();
    Date issuedAt = Date.from(currentInstant);
    Date expiration = Date.from(currentInstant.plusSeconds(REFRESH_EXPIRATION));

    return Jwts.builder()
      .subject(user.getId())
      .issuedAt(issuedAt)
      .expiration(expiration)
      .signWith(getSecretKeyFromString(REFRESH_SECRET))
      .compact();
  }

  public String generateResetPasswordToken(User user) {
    Instant currentInstant = Instant.now();
    Date issuedAt = Date.from(currentInstant);
    Date expiration = Date.from(currentInstant.plusSeconds(RESET_PASSWORD_EXPIRATION));
    String secret = RESET_PASSWORD_SECRET + user.getAccount().getPassword();

    return Jwts.builder()
      .subject(user.getId())
      .claim("email", user.getAccount().getEmail())
      .claim("type", "reset_password")
      .issuedAt(issuedAt)
      .expiration(expiration)
      .signWith(getSecretKeyFromString(secret))
      .compact();
  }

  public Jws<Claims> verifyAccessToken(String token) {
    return parseSignedToken(token, ACCESS_SECRET);
  }

  public Jws<Claims> verifyRefreshToken(String token) {
    return parseSignedToken(token, REFRESH_SECRET);
  }

  public String extractUserIdUnverified(String token) {
    try {
      String[] chunks = token.split("\\.");

      if (chunks.length < 2) {
        throw new AppException(ErrorCode.TOKEN_INVALID);
      }

      String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
      return objectMapper.readTree(payload).get("sub").asText();
    } catch (Exception e) {
      throw new AppException(ErrorCode.TOKEN_INVALID);
    }
  }

  public Jws<Claims> verifyResetPasswordToken(String token, User user) {
    var decoded = parseSignedToken(token, RESET_PASSWORD_SECRET + user.getAccount().getPassword());

    if (!decoded.getPayload().get("type").equals("reset_password")) {
      throw new AppException(ErrorCode.TOKEN_INVALID);
    }

    return decoded;
  }

  private Jws<Claims> parseSignedToken(String token, String secretKey) {
    try {
      return Jwts.parser()
        .verifyWith(getSecretKeyFromString(secretKey))
        .build()
        .parseSignedClaims(token);
    } catch (ExpiredJwtException e) {
      throw new AppException(ErrorCode.TOKEN_EXPIRED);
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.TOKEN_REQUIRED);
    } catch (JwtException e) {
      throw new AppException(ErrorCode.TOKEN_INVALID);
    }
  }

  private SecretKey getSecretKeyFromString(String secret) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] keyBytes = digest.digest(secret.getBytes(StandardCharsets.UTF_8));

      return Keys.hmacShaKeyFor(keyBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not found", e);
    }
  }
}
