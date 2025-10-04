// Define o pacote para os DTOs de relatório.
package com.pefoce.challenge_pefoce.dto.blockchain;


import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;

import java.util.List;


public record CadeiaCustodiaTransferenciaDTO(
  VestigioDTO dadosDoVestigio,
  BlockchainValidateDTO statusIntegridadeCadeia,
  List<TransferenciaDTO> historicoDeTransferencias
) {
}