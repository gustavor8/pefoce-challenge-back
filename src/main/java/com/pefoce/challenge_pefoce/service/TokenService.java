package com.pefoce.challenge_pefoce.service;

import com.pefoce.challenge_pefoce.entity.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Usado para injetar valores de propriedades.
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {
  
  @Value("${api.security.token.secret}")
  private String secret;

  @Value("${api.security.token.access-token-expiration-ms}")
  private long accessTokenExpirationMs;
  @Value("${api.security.token.refresh-token-expiration-ms}")
  private long refreshTokenExpirationMs;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateAccessToken(Users user) {
    return generateToken(user.getUsername(), accessTokenExpirationMs);
  }

  public String generateRefreshToken(Users user) {
    return generateToken(user.getUsername(), refreshTokenExpirationMs);
  }

  private String generateToken(String username, long expirationMs) {
    Instant now = Instant.now();
    Instant expiration = now.plus(expirationMs, ChronoUnit.MILLIS);
    return Jwts.builder()
      .subject(username)
      .issuer("API Challenge Pefoce")
      .issuedAt(Date.from(now))
      .expiration(Date.from(expiration))
      .signWith(getSigningKey(), Jwts.SIG.HS256)
      .compact();
  }

  public String validateToken(String token) {
    try {
      return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
    } catch (Exception e) {
      throw new RuntimeException("Token JWT inválido ou expirado", e);
    }
  }
}