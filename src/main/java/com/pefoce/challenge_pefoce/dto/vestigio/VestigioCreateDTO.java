package com.pefoce.challenge_pefoce.dto.vestigio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public record VestigioCreateDTO(

  @NotBlank(message = "O tipo do vestígio não pode ser vazio")
  @Size(max = 100, message = "O tipo não pode exceder 100 caracteres")
  String tipo,
  
  String descricao,

  @NotBlank(message = "O local de coleta não pode ser vazio")
  @Size(max = 255, message = "O local de coleta não pode exceder 255 caracteres")
  String localColeta,

  @NotNull(message = "A data de coleta não pode ser nula")
  OffsetDateTime dataColeta,

  @NotNull(message = "O ID do responsável atual não pode ser nulo")
  UUID responsavelAtualId
) {
}