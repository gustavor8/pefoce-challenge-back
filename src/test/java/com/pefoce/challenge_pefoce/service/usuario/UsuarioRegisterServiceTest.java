package com.pefoce.challenge_pefoce.service.usuario;

import com.pefoce.challenge_pefoce.dto.usuario.GetUsuarioDTO;
import com.pefoce.challenge_pefoce.dto.usuario.UsuarioRegisterDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioRegisterServiceTest {
  @Mock
  private UsuarioRepository usuarioRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private UsuarioMapper usuarioMapper;
  @InjectMocks
  private UsuarioRegisterService usuarioRegisterService;

  @Test
  @DisplayName("Deve registrar um novo usuário com sucesso")
  void registerUser_comUsernameDisponivel_deveSalvarUsuario() {
    UsuarioRegisterDTO registerDTO = new UsuarioRegisterDTO(
      "novo.usuario", "senha123", "Novo Usuário",
      "novo@email.com", "Analista", "TI", null);
    when(usuarioRepository.findByUsername("novo.usuario")).thenReturn(Optional.empty());
    String senhaHasheada = "senha_criptografada_mock";
    when(passwordEncoder.encode("senha123")).thenReturn(senhaHasheada);
    Usuario usuarioSalvo = new Usuario();
    usuarioSalvo.setId(UUID.randomUUID());
    usuarioSalvo.setUsername("novo.usuario");
    when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
  GetUsuarioDTO dtoEsperado = new GetUsuarioDTO(usuarioSalvo.getId(), "novo.usuario", "Novo Usuário", "novo@email.com", "Analista", "TI", true, OffsetDateTime.now(), OffsetDateTime.now());
    when(usuarioMapper.toDTO(usuarioSalvo)).thenReturn(dtoEsperado);
    GetUsuarioDTO resultadoDTO = usuarioRegisterService.registerUser(registerDTO);
    assertNotNull(resultadoDTO);
    assertEquals(dtoEsperado, resultadoDTO);
    ArgumentCaptor<Usuario> usuarioArgumentCaptor = ArgumentCaptor.forClass(Usuario.class);
    verify(usuarioRepository).save(usuarioArgumentCaptor.capture());
    Usuario usuarioCapturado = usuarioArgumentCaptor.getValue();
    assertEquals("novo.usuario", usuarioCapturado.getUsername());
    assertEquals(senhaHasheada, usuarioCapturado.getPassword());
    assertEquals("Novo Usuário", usuarioCapturado.getNome());
    assertTrue(usuarioCapturado.isAtivo());
  }

  @Test
  @DisplayName("Deve lançar DataIntegrityViolationException para username duplicado")
  void registerUser_comUsernameDuplicado_deveLancarExcecao() {
    UsuarioRegisterDTO registerDTO = new UsuarioRegisterDTO("usuario.existente", "senha123", "Nome", "email@email.com", "Cargo", "Depto", null);
    when(usuarioRepository.findByUsername("usuario.existente")).thenReturn(Optional.of(new Usuario()));
    DataIntegrityViolationException exception = assertThrows(
      DataIntegrityViolationException.class,
      () -> usuarioRegisterService.registerUser(registerDTO)
    );
    assertEquals("Nome de usuário já está em uso.", exception.getMessage());
    verify(passwordEncoder, never()).encode(anyString());
    verify(usuarioRepository, never()).save(any(Usuario.class));
    verify(usuarioMapper, never()).toDTO(any(Usuario.class));
  }
}
