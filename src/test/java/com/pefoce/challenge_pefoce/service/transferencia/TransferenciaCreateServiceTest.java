package com.pefoce.challenge_pefoce.service.transferencia;

import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaCreateDTO;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.entity.vestigio.StatusVestigio;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import com.pefoce.challenge_pefoce.repository.TransferenciaRepository;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import com.pefoce.challenge_pefoce.service.blockchain.BlockchainService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferenciaCreateServiceTest {
  @Mock
  private TransferenciaRepository transferenciaRepository;
  @Mock
  private UsuarioRepository usuarioRepository;
  @Mock
  private VestigioRepository vestigioRepository;
  @Mock
  private TransferenciaMapper transferenciaMapper;
  @Mock
  private BlockchainService blockchainService;
  @InjectMocks
  private TransferenciaCreateService transferenciaCreateService;
  @Captor
  private ArgumentCaptor<Set<Vestigio>> vestigiosCaptor;
  private Usuario responsavelOrigem;
  private Usuario responsavelDestino;
  private Vestigio vestigio;
  @BeforeEach
  void setUp() {
    responsavelOrigem = new Usuario();
    responsavelOrigem.setId(UUID.randomUUID());
    responsavelDestino = new Usuario();
    responsavelDestino.setId(UUID.randomUUID());
    vestigio = new Vestigio();
    vestigio.setId(UUID.randomUUID());
    vestigio.setResponsavelAtual(responsavelOrigem);
  }
  @Test
  @DisplayName("Deve criar a transferência com sucesso quando todos os dados são válidos")
  void criar_comDadosValidos_deveRetornarTransferenciaDTO() {
    TransferenciaCreateDTO createDTO = new TransferenciaCreateDTO(
      Set.of(vestigio.getId()), responsavelDestino.getId(), "Motivo do teste");
    Transferencia transferenciaSalva = new Transferencia();
    TransferenciaDTO dtoEsperado = new TransferenciaDTO(null, null, null, null, null, null, null);
    when(usuarioRepository.findById(responsavelDestino.getId())).thenReturn(Optional.of(responsavelDestino));
    when(vestigioRepository.findAllById(Set.of(vestigio.getId()))).thenReturn(List.of(vestigio));
    when(transferenciaRepository.save(any(Transferencia.class))).thenReturn(transferenciaSalva);
    when(transferenciaMapper.toDTO(transferenciaSalva)).thenReturn(dtoEsperado);
    TransferenciaDTO resultado = transferenciaCreateService.criar(createDTO, responsavelOrigem);
    assertNotNull(resultado);
    assertEquals(dtoEsperado, resultado);
    verify(vestigioRepository).saveAll(vestigiosCaptor.capture());
    Set<Vestigio> vestigiosAtualizados = vestigiosCaptor.getValue();
    Vestigio vestigioAtualizado = vestigiosAtualizados.iterator().next();
    assertEquals(responsavelDestino, vestigioAtualizado.getResponsavelAtual());
    assertEquals(StatusVestigio.EM_ANALISE, vestigioAtualizado.getStatus());
    verify(transferenciaRepository).save(any(Transferencia.class));
    verify(blockchainService).criarNovoBloco(Collections.singleton(transferenciaSalva));
  }

  @Test
  @DisplayName("Deve lançar EntityNotFoundException se o responsável de destino não existir")
  void criar_comDestinoInexistente_deveLancarExcecao() {
    TransferenciaCreateDTO createDTO = new TransferenciaCreateDTO(
      Set.of(vestigio.getId()), responsavelDestino.getId(), "Motivo do teste");
    when(usuarioRepository.findById(responsavelDestino.getId())).thenReturn(Optional.empty());
    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
      () -> transferenciaCreateService.criar(createDTO, responsavelOrigem));
    assertEquals("Responsável de destino não encontrado.", exception.getMessage());
    verify(transferenciaRepository, never()).save(any());
  }
  @Test
  @DisplayName("Deve lançar EntityNotFoundException se um ou mais vestígios não forem encontrados")
  void criar_comVestigioInexistente_deveLancarExcecao() {
    TransferenciaCreateDTO createDTO = new TransferenciaCreateDTO(
      Set.of(vestigio.getId()), responsavelDestino.getId(), "Motivo do teste");
    when(usuarioRepository.findById(responsavelDestino.getId())).thenReturn(Optional.of(responsavelDestino));
    when(vestigioRepository.findAllById(Set.of(vestigio.getId()))).thenReturn(Collections.emptyList());
    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
      () -> transferenciaCreateService.criar(createDTO, responsavelOrigem));
    assertEquals("Um ou mais vestígios não foram encontrados.", exception.getMessage());
  }

  @Test
  @DisplayName("Deve lançar SecurityException se o usuário de origem não tiver a custódia")
  void criar_semCustodiaDoVestigio_deveLancarExcecao() {
    TransferenciaCreateDTO createDTO = new TransferenciaCreateDTO(
      Set.of(vestigio.getId()), responsavelDestino.getId(), "Motivo do teste");
    Usuario outroUsuario = new Usuario();
    outroUsuario.setId(UUID.randomUUID());
    vestigio.setResponsavelAtual(outroUsuario);
    when(usuarioRepository.findById(responsavelDestino.getId())).thenReturn(Optional.of(responsavelDestino));
    when(vestigioRepository.findAllById(Set.of(vestigio.getId()))).thenReturn(List.of(vestigio));
    SecurityException exception = assertThrows(SecurityException.class,
      () -> transferenciaCreateService.criar(createDTO, responsavelOrigem));
    assertEquals("O usuário não tem a custódia do vestígio de ID: " + vestigio.getId(), exception.getMessage());
  }
}

