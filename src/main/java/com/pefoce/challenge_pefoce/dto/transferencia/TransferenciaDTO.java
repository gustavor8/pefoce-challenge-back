package com.pefoce.challenge_pefoce.dto.transferencia;

import com.pefoce.challenge_pefoce.dto.shared.ResponsavelDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioTransferDTO;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record TransferenciaDTO(
  UUID id,
  String motivo,
  OffsetDateTime dataTransferencia,
  String hashTransacao,
  Set<VestigioTransferDTO> vestigios,
  ResponsavelDTO responsavelOrigem,
  ResponsavelDTO responsavelDestino
) {
}