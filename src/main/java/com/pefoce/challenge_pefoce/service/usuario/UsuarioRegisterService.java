package com.pefoce.challenge_pefoce.service.usuario;

import com.pefoce.challenge_pefoce.dto.usuario.GetUsuarioDTO;
import com.pefoce.challenge_pefoce.dto.usuario.UsuarioRegisterDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
// Lançada quando uma regra de integridade do banco de dados é violada.
import org.springframework.dao.DataIntegrityViolationException;
// Usada para criptografar e comparar senhas de forma segura.
import org.springframework.security.crypto.password.PasswordEncoder;
// Marca a classe como um componente da camada de lógica de negócio
import org.springframework.stereotype.Service;
// Garante que um método execute como uma única transação no banco de dados (tudo ou nada).
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioRegisterService {
  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;
  private final UsuarioMapper usuarioMapper;


  @Autowired
  public UsuarioRegisterService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper) {
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
    this.usuarioMapper = usuarioMapper;
  }

  @Transactional
  public GetUsuarioDTO registerUser(UsuarioRegisterDTO usuarioRegisterDTO) {

    if (usuarioRepository.findByUsername(usuarioRegisterDTO.username()).isPresent()) {
      throw new DataIntegrityViolationException("Nome de usuário já está em uso.");
    }
    String hashedPassword = passwordEncoder.encode(usuarioRegisterDTO.password());
    Usuario newUser = Usuario.builder()
      .username(usuarioRegisterDTO.username())
      .password(hashedPassword)
      .nome(usuarioRegisterDTO.nome())
      .email(usuarioRegisterDTO.email())
      .cargo(usuarioRegisterDTO.cargo())
      .departamento(usuarioRegisterDTO.departamento())
      .ativo(true)
      .build();

    Usuario savedUser = usuarioRepository.save(newUser);
    return usuarioMapper.toDTO(savedUser);
  }
}