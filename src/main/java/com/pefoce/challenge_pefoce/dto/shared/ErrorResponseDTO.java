package com.pefoce.challenge_pefoce.dto.shared;

import java.time.Instant;

/**
 * DTO para padronizar as respostas de erro da API.
 * Usando um record do Java 21 para imutabilidade e concisão.
 *
 * @param timestamp Momento em que o erro ocorreu.
 * @param status    Código de status HTTP.
 * @param error     Descrição curta do status HTTP (ex: "Not Found", "Bad Request").
 * @param message   Mensagem detalhada do erro.
 * @param path      O caminho (URI) onde o erro ocorreu.
 */
public record ErrorResponseDTO(
  Instant timestamp,
  Integer status,
  String error,
  String message,
  String path
) {
}