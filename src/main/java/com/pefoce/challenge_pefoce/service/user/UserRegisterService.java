package com.pefoce.challenge_pefoce.service.user;

import com.pefoce.challenge_pefoce.dto.user.GetUserDTO;
import com.pefoce.challenge_pefoce.dto.user.RegisterDTO;
import com.pefoce.challenge_pefoce.entity.Users;
import com.pefoce.challenge_pefoce.repository.UserRepository;
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
public class UserRegisterService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;


  @Autowired
  public UserRegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
  }

  @Transactional
  public GetUserDTO registerUser(RegisterDTO registerDTO) {

    if (userRepository.findByUsername(registerDTO.username()).isPresent()) {
      throw new DataIntegrityViolationException("Nome de usuário já está em uso.");
    }
    String hashedPassword = passwordEncoder.encode(registerDTO.password());
    Users newUser = Users.builder()
      .username(registerDTO.username())
      .password(hashedPassword)
      .nome(registerDTO.nome())
      .email(registerDTO.email())
      .cargo(registerDTO.cargo())
      .departamento(registerDTO.departamento())
      .ativo(true)
      .build();

    Users savedUser = userRepository.save(newUser);
    return userMapper.toDTO(savedUser);
  }
}