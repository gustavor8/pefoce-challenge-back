package com.pefoce.challenge_pefoce.config;

import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.service.auth.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {
  @Mock
  private TokenService tokenService;
  @Mock
  private UsuarioRepository usuarioRepository;
  @Mock
  private HttpServletRequest request;
  @Mock
  private FilterChain filterChain;
  @InjectMocks
  private SecurityFilter securityFilter;
  private MockHttpServletResponse response;
  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    response = new MockHttpServletResponse();
  }
  @Test
  @DisplayName("Cenário 1: Deve permitir a requisição sem autenticar se não houver token")
  void doFilterInternal_semToken_deveApenasChamarProximoFiltro() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);
    securityFilter.doFilterInternal(request, response, filterChain);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Cenário 2: Deve autenticar o usuário com um Access Token válido")
  void doFilterInternal_comAccessTokenValido_deveAutenticarUsuario() throws ServletException, IOException {
    var accessToken = "valid.access.token";
    var username = "user.test";
    var usuario = Usuario.builder().id(UUID.randomUUID()).username(username).build();
    when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken);
    when(tokenService.validateToken(accessToken)).thenReturn(username);
    when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));
    securityFilter.doFilterInternal(request, response, filterChain);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(username);
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Cenário 3: Deve renovar o Access Token se ele estiver expirado e o Refresh Token for válido")
  void doFilterInternal_comAccessTokenExpiradoERefreshTokenValido_deveRenovarTokenEAutenticar() throws ServletException, IOException {
    var expiredAccessToken = "expired.access.token";
    var validRefreshToken = "valid.refresh.token";
    var username = "user.test";
    var newAccessToken = "new.access.token";
    var usuario = Usuario.builder().id(UUID.randomUUID()).username(username).build();
    when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredAccessToken);
    when(tokenService.validateToken(expiredAccessToken)).thenThrow(new RuntimeException("Token expirado"));
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("refreshToken", validRefreshToken)});
    when(tokenService.validateToken(validRefreshToken)).thenReturn(username);
    when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));
    when(tokenService.generateAccessToken(usuario)).thenReturn(newAccessToken);
    securityFilter.doFilterInternal(request, response, filterChain);
    assertThat(response.getHeader("new_access_token")).isEqualTo(newAccessToken);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(username);
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Cenário 4: Não deve autenticar se Access e Refresh Tokens forem inválidos")
  void doFilterInternal_comTokensInvalidos_naoDeveAutenticar() throws ServletException, IOException {
    var expiredAccessToken = "expired.access.token";
    var invalidRefreshToken = "invalid.refresh.token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredAccessToken);
    when(tokenService.validateToken(expiredAccessToken)).thenThrow(new RuntimeException("Token expirado"));
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("refreshToken", invalidRefreshToken)});
    when(tokenService.validateToken(invalidRefreshToken)).thenThrow(new RuntimeException("Token inválido"));
    securityFilter.doFilterInternal(request, response, filterChain);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Cenário 5: Não deve autenticar se Access Token expirou e não há Refresh Token")
  void doFilterInternal_comAccessTokenExpiradoSemRefreshToken_naoDeveAutenticar() throws ServletException, IOException {
    var expiredAccessToken = "expired.access.token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredAccessToken);
    when(tokenService.validateToken(expiredAccessToken)).thenThrow(new RuntimeException("Token expirado"));
    when(request.getCookies()).thenReturn(null);
    securityFilter.doFilterInternal(request, response, filterChain);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Cenário 6: Não deve autenticar se o Refresh Token é válido mas o usuário não existe mais no banco")
  void doFilterInternal_comRefreshTokenValidoMasUsuarioInexistente_naoDeveAutenticar() throws ServletException, IOException {
    var expiredAccessToken = "expired.access.token";
    var validRefreshToken = "valid.refresh.token";
    var username = "user.does.not.exist";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredAccessToken);
    when(tokenService.validateToken(expiredAccessToken)).thenThrow(new RuntimeException("Token expirado"));
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("refreshToken", validRefreshToken)});
    when(tokenService.validateToken(validRefreshToken)).thenReturn(username);
    when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());
    securityFilter.doFilterInternal(request, response, filterChain);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Cenário 7: Deve capturar exceção inesperada e enviar resposta de erro 403")
  void doFilterInternal_comExcecaoInesperada_deveEnviarRespostaDeErro() throws ServletException, IOException {
    var accessToken = "valid.access.token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken);
    when(tokenService.validateToken(accessToken)).thenReturn("user.test");
    when(usuarioRepository.findByUsername("user.test")).thenReturn(Optional.of(Usuario.builder().build()));
    when(request.getRequestURI()).thenReturn("/api/some/path"); // Adicionado para evitar NullPointerException na criação do DTO de erro
    doThrow(new ServletException("Erro simulado no proximo filtro")).when(filterChain).doFilter(request, response);
    securityFilter.doFilterInternal(request, response, filterChain);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    assertThat(response.getContentAsString()).contains("Erro simulado no proximo filtro");
  }
}