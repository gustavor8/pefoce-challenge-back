package com.pefoce.challenge_pefoce.dto.transferencia;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record TransferenciaCreateDTO(

  @NotEmpty(message = "É necessário informar ao menos um ID de vestígio")
  Set<UUID> vestigioIds,

  @NotNull(message = "O ID do responsável de destino não pode ser nulo")
  UUID responsavelDestinoId,

  String motivo
) {
}