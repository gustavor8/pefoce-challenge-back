package com.pefoce.challenge_pefoce.dto.blockchain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlockchainValidateDTO(

  @NotNull(message = "O status de validade não pode ser nulo")
  Boolean valid,

  @NotBlank(message = "A mensagem de validação não pode ser vazia")
  @Size(max = 1000, message = "A mensagem não pode exceder 1000 caracteres")
  String message
) {
}