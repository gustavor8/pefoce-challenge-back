package com.pefoce.challenge_pefoce.config;

import com.fasterxml.jackson.databind.ObjectMapper; // Para converter o erro em JSON.
import com.pefoce.challenge_pefoce.dto.shared.ErrorResponseDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.service.util.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;    // Para definir o Content-Type.
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
// Marca a classe como um componente gerenciado pelo Spring.
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final UsuarioRepository usuarioRepository;

  public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
    this.tokenService = tokenService;
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

    try {
      var token = recoverToken(request);

      if (token!=null) {
        var username = tokenService.validateToken(token);
        Optional<Usuario> userOptional = usuarioRepository.findByUsername(username);
        userOptional.ifPresent(user -> {
          var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(authentication);
        });
      }

      filterChain.doFilter(request, response);

    } catch (RuntimeException e) {

      ErrorResponseDTO errorResponse = new ErrorResponseDTO(
        Instant.now(),
        HttpStatus.FORBIDDEN.value(), // Status 403
        "Token Inválido",
        "O token de acesso é inválido ou expirado. Por favor, autentique-se novamente.",
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

  private String recoverToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader==null || !authHeader.startsWith("Bearer ")) {
      return null;
    }
    return authHeader.substring(7);
  }
}