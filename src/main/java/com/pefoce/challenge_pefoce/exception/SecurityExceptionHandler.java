package com.pefoce.challenge_pefoce.exception;


import com.fasterxml.jackson.databind.ObjectMapper; // converte objetos Java em JSON
import com.pefoce.challenge_pefoce.dto.shared.ErrorResponseDTO;
import jakarta.servlet.ServletException; // execeção de Servlets
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // tipos de dados
import org.springframework.security.access.AccessDeniedException; // erro 403
import org.springframework.security.core.AuthenticationException; // erro 401
import org.springframework.security.web.AuthenticationEntryPoint; // trata falha de autenticação
import org.springframework.security.web.access.AccessDeniedHandler; //  trata falha de autorização
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;

@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
    throws IOException, ServletException {

    ErrorResponseDTO errorResponse = new ErrorResponseDTO(
      Instant.now(),
      HttpStatus.UNAUTHORIZED.value(),
      "Não Autenticado",
      "É necessário autenticação para acessar este recurso.",
      request.getRequestURI()
    );

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    OutputStream responseStream = response.getOutputStream();
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    mapper.writeValue(responseStream, errorResponse);
    responseStream.flush();
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
    throws IOException, ServletException {

    ErrorResponseDTO errorResponse = new ErrorResponseDTO(
      Instant.now(),
      HttpStatus.FORBIDDEN.value(),
      "Acesso Negado",
      "Você não tem permissão para acessar este recurso.",
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