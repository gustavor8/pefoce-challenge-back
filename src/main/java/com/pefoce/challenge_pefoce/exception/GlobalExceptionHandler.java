package com.pefoce.challenge_pefoce.exception;

import com.pefoce.challenge_pefoce.dto.shared.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.AuthenticationException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // ERRO 400: Bad Request
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
    String path = request.getDescription(false).replace("uri=", "");
    String details = ex.getBindingResult().getFieldErrors().stream()
      .map(error -> "'" + error.getField() + "': " + error.getDefaultMessage())
      .collect(Collectors.joining(", "));
    String message = "Erro de validação: " + details;
    logger.warn("Dados inválidos para o path: {}. Detalhes: {}", path, message);
    return buildErrorResponse(HttpStatus.BAD_REQUEST, message, path);
  }

  // ERRO 400: Bad Request
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
    String path = request.getDescription(false).replace("uri=", "");
    String message = "O corpo da requisição está malformado ou é inválido.";
    logger.warn("JSON malformado para o path: {}. Causa: {}", path, ex.getRootCause()!=null ? ex.getRootCause().getMessage():ex.getMessage());
    return buildErrorResponse(HttpStatus.BAD_REQUEST, message, path);
  }

  // ERRO 403: Forbidden
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
    String path = request.getDescription(false).replace("uri=", "");
    String message = "Usuário ou senha inválidos.";
    logger.warn("Falha na autenticação para o path: {}. Motivo: {}", path, ex.getMessage());
    return buildErrorResponse(HttpStatus.FORBIDDEN, message, path);
  }

  // ERRO 404: Not Found
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
    String path = request.getDescription(false).replace("uri=", "");
    String message = "O recurso solicitado não foi encontrado.";
    logger.warn("Recurso não encontrado para o path: {}. Detalhes: {}", path, ex.getMessage());
    return buildErrorResponse(HttpStatus.NOT_FOUND, message, path);
  }

  // ERRO 405: Method Not Allowed
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponseDTO> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
    String path = request.getDescription(false).replace("uri=", "");
    String message = String.format("O método '%s' não é suportado para esta URL. Métodos suportados: %s",
      ex.getMethod(), ex.getSupportedHttpMethods());
    logger.warn("Método HTTP não suportado para o path: {}. Detalhes: {}", path, message);
    return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, message, path);
  }

  // ERRO 409: Conflict
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
    String path = request.getDescription(false).replace("uri=", "");
    String message = "Conflito de dados. O recurso pode já existir ou viola uma regra de negócio.";
    logger.warn("Violação de integridade de dados para o path: {}. Causa: {}", path, ex.getRootCause()!=null ? ex.getRootCause().getMessage():ex.getMessage());
    return buildErrorResponse(HttpStatus.CONFLICT, message, path);
  }

  // ERRO 500: Internal Server Error
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, WebRequest request) {
    String path = request.getDescription(false).replace("uri=", "");
    String message = "Ocorreu um erro inesperado no servidor. Por favor, tente novamente mais tarde.";
    logger.error("Erro inesperado (500) para o path: {}", path, ex);
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, path);
  }


  private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String message, String path) {
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(
      Instant.now(),
      status.value(),
      status.getReasonPhrase(),
      message,
      path
    );
    return new ResponseEntity<>(errorResponse, status);
  }
}