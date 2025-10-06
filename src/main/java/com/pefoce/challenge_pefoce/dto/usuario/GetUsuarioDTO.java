package com.pefoce.challenge_pefoce.dto.usuario;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetUsuarioDTO(
  UUID id,
  String username,
  String nome,
  String email,
  String cargo,
  String departamento,
  boolean ativo,
  OffsetDateTime criadoEm,
  OffsetDateTime atualizadoEm
) {
}