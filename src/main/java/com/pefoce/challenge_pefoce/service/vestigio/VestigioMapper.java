package com.pefoce.challenge_pefoce.service.vestigio;


import com.pefoce.challenge_pefoce.dto.shared.ResponsavelDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;

import com.pefoce.challenge_pefoce.entity.Users;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;

import org.springframework.stereotype.Component;

@Component
public class VestigioMapper {

  public VestigioDTO toDTO(Vestigio vestigio) {
    return new VestigioDTO(
      vestigio.getId(),
      vestigio.getTipo(),
      vestigio.getDescricao(),
      vestigio.getLocalColeta(),
      vestigio.getDataColeta(),
      vestigio.getStatus().name(), // Converte o Enum para seu nome em String.
      vestigio.getCriadoEm(),
      vestigio.getAtualizadoEm(),
      // converte a entidade Users aninhada.
      toResponsavelDTO(vestigio.getResponsavelAtual())
    );
  }

  // converte Users em ResponsavelDTO
  private ResponsavelDTO toResponsavelDTO(Users user) {
    if (user==null) return null;
    return new ResponsavelDTO(user.getId(), user.getNome());
  }
}