package com.pefoce.challenge_pefoce.service.user;

import com.pefoce.challenge_pefoce.dto.usuario.GetUsuarioDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {


  public GetUsuarioDTO toDTO(Usuario user) {
    return new GetUsuarioDTO(
      user.getId(),
      user.getUsername(),
      user.getNome(),
      user.getEmail(),
      user.getCargo(),
      user.getDepartamento(),
      user.isAtivo(),
      user.getCriadoEm(),
      user.getAtualizadoEm()
    );
  }
}