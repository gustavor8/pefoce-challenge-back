package com.pefoce.challenge_pefoce.dto.vestigio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VestigioUpdateDTO(

  @NotBlank(message = "O tipo do vestígio não pode ser vazio")
  @Size(max = 100, message = "O tipo não pode exceder 100 caracteres")
  String tipo,

  String descricao,

  @NotBlank(message = "O local de coleta não pode ser vazio")
  @Size(max = 255, message = "O local de coleta não pode exceder 255 caracteres")
  String localColeta,

  @NotBlank(message = "O status não pode ser vazio")
  @Size(max = 50, message = "O status não pode exceder 50 caracteres")
  String status
) {
}