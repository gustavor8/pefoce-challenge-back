package com.pefoce.challenge_pefoce.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
  @NotBlank(message = "O nome de usuário não pode ser vazio")
  @Size(max = 100, message = "O nome de usuário não pode exceder 100 caracteres")
  String username,
  @NotBlank(message = "A senha não pode ser vazia")
  @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
  String password,
  @NotBlank(message = "O nome não pode ser vazio")
  @Size(max = 255, message = "O nome não pode exceder 255 caracteres")
  String nome,
  @NotBlank(message = "O email não pode ser vazio")
  @Email(message = "Formato de email inválido")
  @Size(max = 255, message = "O email não pode exceder 255 caracteres")
  String email,
  @Size(max = 100, message = "O cargo não pode exceder 100 caracteres")
  String cargo,
  @Size(max = 100, message = "O departamento não pode exceder 100 caracteres")
  String departamento,
  String certificadoDigital
) {
}