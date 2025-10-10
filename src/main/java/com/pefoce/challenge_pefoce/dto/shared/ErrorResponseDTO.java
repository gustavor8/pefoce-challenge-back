package com.pefoce.challenge_pefoce.dto.shared;

import java.time.Instant;

public record ErrorResponseDTO(
  Instant timestamp,
  Integer status,
  String error,
  String message,
  String path
) {
}