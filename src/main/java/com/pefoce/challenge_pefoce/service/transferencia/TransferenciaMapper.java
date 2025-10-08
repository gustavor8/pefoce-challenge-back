package com.pefoce.challenge_pefoce.service.transferencia;

import com.pefoce.challenge_pefoce.dto.usuario.UsuarioResponsavelDTO;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioTransferDTO;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TransferenciaMapper {


  public TransferenciaDTO toDTO(Transferencia transferencia) {
    return new TransferenciaDTO(
      transferencia.getId(),
      transferencia.getMotivo(),
      transferencia.getDataTransferencia(),
      transferencia.getHashTransacao(),
      // Mapeia o conjunto de entidades Vestigio para um conjunto de DTOs de vestígio.
      transferencia.getVestigios().stream()
        .map(this::toVestigioTransferDTO)
        .collect(Collectors.toSet()),
      // Mapeia as entidades de origem e destino para DTOs de responsável.
      toResponsavelDTO(transferencia.getResponsavelOrigem()),
      toResponsavelDTO(transferencia.getResponsavelDestino())
    );
  }

  private UsuarioResponsavelDTO toResponsavelDTO(Usuario user) {
    if (user==null) return null;
    return new UsuarioResponsavelDTO(user.getId(), user.getNome());
  }


  private VestigioTransferDTO toVestigioTransferDTO(Vestigio vestigio) {
    if (vestigio==null) return null;
    return new VestigioTransferDTO(vestigio.getId(), vestigio.getTipo());
  }
}