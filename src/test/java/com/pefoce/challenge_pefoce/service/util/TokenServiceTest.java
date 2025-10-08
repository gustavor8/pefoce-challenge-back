package com.pefoce.challenge_pefoce.service.util;

import com.pefoce.challenge_pefoce.entity.Usuario;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils; // Classe do spring pra alterar campos privados nos testes.
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
  @InjectMocks
  private TokenService tokenService;
  @Mock
  private Usuario usuarioMock;
  private final String secretKey = "MinhaChaveSecretaSuperSeguraParaTestesUnitarios123";
  private final long accessTokenExpiration = 3600000; // 1 hora
  private final long refreshTokenExpiration = 86400000; // 24 horas
  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(tokenService, "secret", secretKey);
    ReflectionTestUtils.setField(tokenService, "accessTokenExpirationMs", accessTokenExpiration);
    ReflectionTestUtils.setField(tokenService, "refreshTokenExpirationMs", refreshTokenExpiration);
  }

  @Test
  @DisplayName("Deve gerar um Access Token válido para um usuário")
  void generateAccessToken_deveCriarTokenValido() {
    String username = "usuario.teste";
    when(usuarioMock.getUsername()).thenReturn(username);
    String accessToken = tokenService.generateAccessToken(usuarioMock);
    assertNotNull(accessToken);
    assertFalse(accessToken.isBlank());
    assertEquals(username, tokenService.validateToken(accessToken));
  }

  @Test
  @DisplayName("Deve gerar um Refresh Token válido para um usuário")
  void generateRefreshToken_deveCriarTokenValido() {
    String username = "usuario.teste";
    when(usuarioMock.getUsername()).thenReturn(username);
    String refreshToken = tokenService.generateRefreshToken(usuarioMock);
    assertNotNull(refreshToken);
    assertFalse(refreshToken.isBlank());
    assertEquals(username, tokenService.validateToken(refreshToken));
  }

  @Test
  @DisplayName("Deve lançar RuntimeException ao validar um token inválido")
  void validateToken_comTokenInvalido_deveLancarExcecao() {
    String tokenInvalido = "jwt.token.invalido";
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      tokenService.validateToken(tokenInvalido);
    });
    assertEquals("Token JWT inválido ou expirado", exception.getMessage());
  }

  @Test
  @DisplayName("Deve lançar RuntimeException ao validar um token expirado")
  void validateToken_comTokenExpirado_deveLancarExcecao() {
    ReflectionTestUtils.setField(tokenService, "accessTokenExpirationMs", -1000L);
    String username = "usuario.expirado";
    when(usuarioMock.getUsername()).thenReturn(username);
    String tokenExpirado = tokenService.generateAccessToken(usuarioMock);
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      tokenService.validateToken(tokenExpirado);
    });
    assertEquals("Token JWT inválido ou expirado", exception.getMessage());
    assertInstanceOf(ExpiredJwtException.class, exception.getCause());
  }
}
