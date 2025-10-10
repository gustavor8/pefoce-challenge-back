package com.pefoce.challenge_pefoce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pefoce.challenge_pefoce.dto.shared.ErrorResponseDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.service.auth.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Arrays;

@Component
public class SecurityFilter extends OncePerRequestFilter {
  private final TokenService tokenService;
  private final UsuarioRepository usuarioRepository;
  public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
    this.tokenService = tokenService;
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
    try {
      var accessToken = recoverToken(request);
      if (accessToken != null) {
        try {
          var username = tokenService.validateToken(accessToken);
          autenticarUsuario(username);
        } catch (RuntimeException ex) {
          var refreshToken = recoverRefreshToken(request);
          if (refreshToken != null) {
            try {
              var username = tokenService.validateToken(refreshToken);
              String newAccessToken = gerarNovoAccessToken(username);
              response.setHeader("new_access_token", newAccessToken);
              autenticarUsuario(username);
            } catch (RuntimeException e) {
              System.err.println("Refresh Token inválido: " + e.getMessage());
            }
          }
        }
      }
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      sendErrorResponse(request, response, e);
    }
  }
  private String recoverToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }
    return authHeader.substring(7);
  }

  private void autenticarUsuario(String username) {
    usuarioRepository.findByUsername(username).ifPresent(user -> {
      var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    });
  }
  private String recoverRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }
    return Arrays.stream(cookies)
      .filter(cookie -> "refreshToken".equals(cookie.getName()))
      .map(Cookie::getValue)
      .findFirst()
      .orElse(null);
  }
  private String gerarNovoAccessToken(String username) {
    Usuario user = usuarioRepository.findByUsername(username)
      .orElseThrow(() -> new RuntimeException("Usuário do token não encontrado"));
    return tokenService.generateAccessToken(user);
  }


  private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(
      Instant.now(),
      HttpStatus.FORBIDDEN.value(),
      "Token Inválido",
      e.getMessage(),
      request.getRequestURI()
    );
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    OutputStream responseStream = response.getOutputStream();
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    mapper.writeValue(responseStream, errorResponse);
    responseStream.flush();
  }
}