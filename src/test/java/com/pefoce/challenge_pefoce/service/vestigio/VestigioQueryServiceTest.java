package com.pefoce.challenge_pefoce.service.vestigio;

import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioStatusResponseDTO;
import com.pefoce.challenge_pefoce.entity.vestigio.StatusVestigio;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VestigioQueryServiceTest {
  @Mock
  private VestigioRepository vestigioRepository;
  @Mock
  private VestigioMapper vestigioMapper;
  @InjectMocks
  private VestigioQueryService vestigioQueryService;

  @Test
  @DisplayName("listarTodos - Deve retornar uma lista de VestigioDTO quando houver vestígios")
  void listarTodos_quandoExistemVestigios_deveRetornarListaDeDTOs() {
    Vestigio vestigioMock = mock(Vestigio.class);
    VestigioDTO vestigioDTOMock = mock(VestigioDTO.class);
    when(vestigioRepository.findAll()).thenReturn(List.of(vestigioMock));
    when(vestigioMapper.toDTO(vestigioMock)).thenReturn(vestigioDTOMock);
    List<VestigioDTO> resultado = vestigioQueryService.listarTodos();
    assertNotNull(resultado);
    assertFalse(resultado.isEmpty());
    assertEquals(1, resultado.size());
    assertEquals(vestigioDTOMock, resultado.getFirst());
    verify(vestigioMapper, times(1)).toDTO(vestigioMock); // Garante que o mapper foi chamado.
  }

  @Test
  @DisplayName("listarTodos - Deve retornar uma lista vazia quando não houver vestígios")
  void listarTodos_quandoNaoExistemVestigios_deveRetornarListaVazia() {
    when(vestigioRepository.findAll()).thenReturn(Collections.emptyList());
    List<VestigioDTO> resultado = vestigioQueryService.listarTodos();
    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(vestigioMapper, never()).toDTO(any()); // Garante que o mapper nunca foi chamado.
  }

  @Test
  @DisplayName("buscarPorId - Deve retornar VestigioDTO quando o ID existe")
  void buscarPorId_quandoIdExiste_deveRetornarDTO() {
    UUID id = UUID.randomUUID();
    Vestigio vestigioMock = mock(Vestigio.class);
    VestigioDTO vestigioDTOMock = mock(VestigioDTO.class);
    when(vestigioRepository.findById(id)).thenReturn(Optional.of(vestigioMock));
    when(vestigioMapper.toDTO(vestigioMock)).thenReturn(vestigioDTOMock);
    VestigioDTO resultado = vestigioQueryService.buscarPorId(id);
    assertNotNull(resultado);
    assertEquals(vestigioDTOMock, resultado);
  }

  @Test
  @DisplayName("buscarPorId - Deve lançar EntityNotFoundException quando o ID não existe")
  void buscarPorId_quandoIdNaoExiste_deveLancarExcecao() {
    UUID id = UUID.randomUUID();
    when(vestigioRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> vestigioQueryService.buscarPorId(id));
    verify(vestigioMapper, never()).toDTO(any()); // O mapper não deve ser chamado se o vestígio não for encontrado.
  }

  @Test
  @DisplayName("buscarStatusPorId - Deve retornar DTO de status quando o vestígio é encontrado")
  void buscarStatusPorId_quandoVestigioExiste_deveRetornarStatusDTO() {
    UUID id = UUID.randomUUID();
    Vestigio vestigioMock = new Vestigio();
    vestigioMock.setId(id);
    vestigioMock.setStatus(StatusVestigio.EM_ANALISE);
    when(vestigioRepository.findById(id)).thenReturn(Optional.of(vestigioMock));
    VestigioStatusResponseDTO resultado = vestigioQueryService.buscarStatusPorId(id);
    assertNotNull(resultado);
    assertEquals(id, resultado.id());
    assertEquals("EM_ANALISE", resultado.status());
  }

  @Test
  @DisplayName("buscarStatusPorId - Deve lançar EntityNotFoundException quando o vestígio não é encontrado")
  void buscarStatusPorId_quandoVestigioNaoExiste_deveLancarExcecao() {
    UUID id = UUID.randomUUID();
    when(vestigioRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> vestigioQueryService.buscarStatusPorId(id));
  }
}