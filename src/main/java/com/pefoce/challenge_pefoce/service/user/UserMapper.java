package com.pefoce.challenge_pefoce.service.user;

import com.pefoce.challenge_pefoce.dto.user.GetUserDTO;
import com.pefoce.challenge_pefoce.entity.Users;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {


  public GetUserDTO toDTO(Users user) {
    return new GetUserDTO(
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