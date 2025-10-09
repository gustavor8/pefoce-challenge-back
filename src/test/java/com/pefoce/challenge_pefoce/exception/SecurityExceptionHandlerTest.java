// Define o pacote onde a classe de teste está localizada.
package com.pefoce.challenge_pefoce.exception;

// Importações de bibliotecas e classes necessárias para os testes.
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pefoce.challenge_pefoce.dto.shared.ErrorResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityExceptionHandlerTest {
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private AuthenticationException authException;
  @Mock
  private AccessDeniedException accessDeniedException;
  @InjectMocks
  private SecurityExceptionHandler securityExceptionHandler;
  private ByteArrayOutputStream responseOutputStream;

  @BeforeEach
  void setUp() throws IOException {
    responseOutputStream = new ByteArrayOutputStream();
    when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(responseOutputStream));
  }

  @Test
  @DisplayName("Deve tratar AuthenticationException e escrever uma resposta 401 Unauthorized")
  void commence_deveEscreverResposta401Unauthorized() throws IOException, ServletException {
    when(request.getRequestURI()).thenReturn("/recurso/protegido");
    securityExceptionHandler.commence(request, response, authException);
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    ErrorResponseDTO errorResponse = mapper.readValue(responseOutputStream.toString(), ErrorResponseDTO.class);
    assertNotNull(errorResponse);
    assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.status());
    assertEquals("Não Autenticado", errorResponse.error());
    assertEquals("É necessário autenticação para acessar este recurso.", errorResponse.message());
    assertEquals("/recurso/protegido", errorResponse.path());
  }

  @Test
  @DisplayName("Deve tratar AccessDeniedException e escrever uma resposta 403 Forbidden")
  void handle_deveEscreverResposta403Forbidden() throws IOException, ServletException {
    when(request.getRequestURI()).thenReturn("/recurso/admin");
    securityExceptionHandler.handle(request, response, accessDeniedException);
    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    ErrorResponseDTO errorResponse = mapper.readValue(responseOutputStream.toString(), ErrorResponseDTO.class);
    assertNotNull(errorResponse);
    assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.status());
    assertEquals("Acesso Negado", errorResponse.error());
    assertEquals("Você não tem permissão para acessar este recurso.", errorResponse.message());
    assertEquals("/recurso/admin", errorResponse.path());
  }

  // classe auxiliar que converte OutputStream (vem do java IO em bytes) em um ServletOutputStream (Vem do javax servelet usada com HTTP)
  private static class DelegatingServletOutputStream extends ServletOutputStream {
    private final OutputStream targetStream;

    public DelegatingServletOutputStream(OutputStream targetStream) {
      this.targetStream = targetStream;
    }

    @Override
    public void write(int b) throws IOException {
      targetStream.write(b);
    }

    @Override
    public void flush() throws IOException {
      targetStream.flush();
    }

    @Override
    public void close() throws IOException {
      targetStream.close();
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
      // sobrescrito só pra não da erro, não precisei nos testes
    }
  }
}