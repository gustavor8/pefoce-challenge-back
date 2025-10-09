package com.pefoce.challenge_pefoce.exception;

import com.pefoce.challenge_pefoce.dto.shared.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult; // Interface de resultados de validação
import org.springframework.validation.FieldError; // Representar um erro no campo
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException; // Falha de validação nas DTO
import org.springframework.web.context.request.WebRequest;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
  @Mock
  private WebRequest webRequest;
  @InjectMocks
  private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setUp() {
    when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
  }

  @Test
  @DisplayName("Deve tratar MethodArgumentNotValidException (Erro 400)")
  void handleValidationException() {
    var bindingResult = mock(BindingResult.class);
    var fieldError = new FieldError("objeto", "campo", "não pode ser nulo");
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
    var methodParameter = mock(MethodParameter.class);
    var ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
    ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleValidationException(ex, webRequest);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(Objects.requireNonNull(response.getBody()).message().contains("'campo': não pode ser nulo"));
  }

  @Test
  @DisplayName("Deve tratar HttpMessageNotReadableException (Erro 400)")
  void handleHttpMessageNotReadable() {
    var httpInputMessage = mock(HttpInputMessage.class);
    var ex = new HttpMessageNotReadableException("JSON malformado", httpInputMessage);
  ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("O corpo da requisição está malformado ou é inválido.", Objects.requireNonNull(response.getBody()).message());
  }

  @Test
  @DisplayName("Deve tratar AuthenticationException (Erro 403)")
  void handleAuthenticationException() {
    var ex = new BadCredentialsException("Credenciais ruins");
    ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleAuthenticationException(ex, webRequest);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals("Usuário ou senha inválidos.", Objects.requireNonNull(response.getBody()).message());
  }

  @Test
  @DisplayName("Deve tratar EntityNotFoundException (Erro 404)")
  void handleEntityNotFound() {
    var ex = new EntityNotFoundException("Recurso não encontrado");
    ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleEntityNotFound(ex, webRequest);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("O recurso solicitado não foi encontrado.", Objects.requireNonNull(response.getBody()).message());
  }

  @Test
  @DisplayName("Deve tratar HttpRequestMethodNotSupportedException (Erro 405)")
  void handleMethodNotSupported() {
    var ex = new HttpRequestMethodNotSupportedException("POST", Set.of(HttpMethod.GET.name()));
    ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleMethodNotSupported(ex, webRequest);
    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    assertTrue(Objects.requireNonNull(response.getBody()).message().contains("O método 'POST' não é suportado"));
  }

  @Test
  @DisplayName("Deve tratar DataIntegrityViolationException (Erro 409)")
  void handleDataIntegrityViolation() {
    var ex = new DataIntegrityViolationException("Violação de constraint");
    ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleDataIntegrityViolation(ex, webRequest);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Conflito de dados. O recurso pode já existir ou viola uma regra de negócio.", Objects.requireNonNull(response.getBody()).message());
  }

  @Test
  @DisplayName("Deve tratar Exception genérica (Erro 500)")
  void handleGenericException() {
    var ex = new NullPointerException("Erro inesperado");
    ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleGenericException(ex, webRequest);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Ocorreu um erro inesperado no servidor. Por favor, tente novamente mais tarde.", Objects.requireNonNull(response.getBody()).message());
  }
}