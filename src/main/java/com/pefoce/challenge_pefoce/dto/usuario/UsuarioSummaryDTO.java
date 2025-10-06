package com.pefoce.challenge_pefoce.dto.usuario;

import java.util.UUID;

public record UsuarioSummaryDTO(
  UUID id,
  String nome
) {
}