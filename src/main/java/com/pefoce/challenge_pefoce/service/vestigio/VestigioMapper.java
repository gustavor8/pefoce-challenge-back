package com.pefoce.challenge_pefoce.service.vestigio;


import com.pefoce.challenge_pefoce.dto.usuario.UsuarioResponsavelDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;

import com.pefoce.challenge_pefoce.entity.Usuario;
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
      // converte a entidade Usuario aninhada.
      toResponsavelDTO(vestigio.getResponsavelAtual())
    );
  }

  // converte Usuario em UsuarioResponsavelDTO
  private UsuarioResponsavelDTO toResponsavelDTO(Usuario user) {
    if (user==null) return null;
    return new UsuarioResponsavelDTO(user.getId(), user.getNome());
  }
}