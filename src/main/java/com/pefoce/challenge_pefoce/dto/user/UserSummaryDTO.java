package com.pefoce.challenge_pefoce.dto.user;

import java.util.UUID;

public record UserSummaryDTO(
  UUID id,
  String nome
) {
}