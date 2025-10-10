package com.pefoce.challenge_pefoce.service.auth;

import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
  @Mock
  private UsuarioRepository usuarioRepository;
  @InjectMocks
  private AuthenticationService authenticationService;

  @Test
  @DisplayName("Deve retornar UserDetails quando o usuário é encontrado pelo username")
  void loadUserByUsername_quandoUsuarioExiste_deveRetornarUserDetails() {
    String usernameExistente = "carlos.silva";
    Usuario usuarioMock = mock(Usuario.class);
    when(usuarioRepository.findByUsername(usernameExistente)).thenReturn(Optional.of(usuarioMock));
    UserDetails userDetails = authenticationService.loadUserByUsername(usernameExistente);
    assertNotNull(userDetails, "O UserDetails retornado não deveria ser nulo.");
    assertEquals(usuarioMock, userDetails, "O UserDetails retornado deveria ser o mesmo que o mock do repositório.");
    verify(usuarioRepository, times(1)).findByUsername(usernameExistente);
  }

  @Test
  @DisplayName("Deve lançar UsernameNotFoundException quando o usuário não é encontrado")
  void loadUserByUsername_quandoUsuarioNaoExiste_deveLancarExcecao() {
    String usernameInexistente = "fantasma";
    when(usuarioRepository.findByUsername(usernameInexistente)).thenReturn(Optional.empty());
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
      () -> authenticationService.loadUserByUsername(usernameInexistente));
    String mensagemEsperada = "Usuário não encontrado com o nome: " + usernameInexistente;
    assertEquals(mensagemEsperada, exception.getMessage(), "A mensagem da exceção não é a esperada.");
    verify(usuarioRepository, times(1)).findByUsername(usernameInexistente);
  }
}

