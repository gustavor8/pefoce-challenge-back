package com.pefoce.challenge_pefoce.dto.vestigio;

import com.pefoce.challenge_pefoce.dto.shared.ResponsavelDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public record VestigioDTO(
  UUID id,
  String tipo,
  String descricao,
  String localColeta,
  OffsetDateTime dataColeta,
  String status,
  OffsetDateTime criadoEm,
  OffsetDateTime atualizadoEm,
  ResponsavelDTO responsavelAtual
) {
}