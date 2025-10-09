package com.pefoce.challenge_pefoce.util;

import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaCreateDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import com.pefoce.challenge_pefoce.service.transferencia.TransferenciaCreateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {
  @Mock
  private UsuarioRepository usuarioRepository;
  @Mock
  private VestigioRepository vestigioRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private TransferenciaCreateService transferenciaCreateService;
  @Mock
  private ApplicationReadyEvent event; // Mock de evento do Spring.
 @InjectMocks
  private DataSeeder dataSeeder;

  // captura para veriricar os usuarios passados
  @Captor
  private ArgumentCaptor<List<Usuario>> usuariosCaptor;

  @Test
  @DisplayName("Não deve popular dados se o usuário 'enio.perito' já existir")
  void onApplicationEvent_quandoEnioPeritoExiste_naoDevePopularDados() {
    when(usuarioRepository.findByUsername("enio.perito")).thenReturn(Optional.of(new Usuario()));
    dataSeeder.onApplicationEvent(event);
    verify(usuarioRepository).findByUsername("enio.perito");
    verifyNoInteractions(passwordEncoder, vestigioRepository, transferenciaCreateService);
    verify(usuarioRepository, never()).saveAll(anyList());
  }

  @Test
  @DisplayName("Não deve popular dados se 'emanuel.perito' existe e contagem de usuários é alta")
  void onApplicationEvent_quandoEmanuelExisteEContagemAlta_naoDevePopularDados() {
    when(usuarioRepository.findByUsername("enio.perito")).thenReturn(Optional.empty());
    when(usuarioRepository.findByUsername("emanuel.perito")).thenReturn(Optional.of(new Usuario()));
    when(usuarioRepository.count()).thenReturn(2L);
    dataSeeder.onApplicationEvent(event);
    verify(usuarioRepository).findByUsername("enio.perito");
    verify(usuarioRepository).findByUsername("emanuel.perito");
    verify(usuarioRepository).count();
    verifyNoInteractions(passwordEncoder, vestigioRepository, transferenciaCreateService);
    verify(usuarioRepository, never()).saveAll(anyList());
  }

  @Test
  @DisplayName("Deve popular o banco de dados quando os usuários de seed não existem")
  void onApplicationEvent_quandoBancoVazio_devePopularTodosOsDados() {
    when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
    setupMocksForSeeding();
    dataSeeder.onApplicationEvent(event);
    verify(usuarioRepository).findByUsername("enio.perito");
    verify(usuarioRepository).findByUsername("emanuel.perito");
    verify(usuarioRepository, never()).count();
    assertSeedingOccurred();
  }

  @Test
  @DisplayName("Deve popular dados se 'emanuel.perito' existe mas contagem de usuários é baixa")
  void onApplicationEvent_quandoApenasUmUsuarioExisteEContagemBaixa_devePopularDados() {
    when(usuarioRepository.findByUsername("enio.perito")).thenReturn(Optional.empty());
    when(usuarioRepository.findByUsername("emanuel.perito")).thenReturn(Optional.of(new Usuario()));
    when(usuarioRepository.count()).thenReturn(1L);
    setupMocksForSeeding();
    dataSeeder.onApplicationEvent(event);
    verify(usuarioRepository).findByUsername("enio.perito");
    verify(usuarioRepository).findByUsername("emanuel.perito");
    verify(usuarioRepository).count();
    assertSeedingOccurred();
  }

  private void setupMocksForSeeding() {
    when(passwordEncoder.encode(anyString())).thenReturn("senha_criptografada");
    when(usuarioRepository.saveAll(anyList())).thenAnswer(invocation -> {
      List<Usuario> users = invocation.getArgument(0);
      users.forEach(user -> {
        if (user.getId() == null) user.setId(UUID.randomUUID());
      });
      return users;
    });
    when(vestigioRepository.saveAll(anyList())).thenAnswer(invocation -> {
      List<Vestigio> vestiges = invocation.getArgument(0);
      vestiges.forEach(vestigio -> {
        if (vestigio.getId() == null) vestigio.setId(UUID.randomUUID());
      });
      return vestiges;
    });
  }

  private void assertSeedingOccurred() {
    verify(passwordEncoder, times(2)).encode("senha123");
    verify(usuarioRepository).saveAll(usuariosCaptor.capture());
    List<Usuario> usuariosSalvos = usuariosCaptor.getValue();
    assertEquals(2, usuariosSalvos.size());
    assertEquals("emanuel.perito", usuariosSalvos.getFirst().getUsername());
    assertEquals("enio.perito", usuariosSalvos.getLast().getUsername());
    verify(vestigioRepository).saveAll(anyList());
    verify(transferenciaCreateService, times(5)).criar(any(TransferenciaCreateDTO.class), any(Usuario.class));
  }
}