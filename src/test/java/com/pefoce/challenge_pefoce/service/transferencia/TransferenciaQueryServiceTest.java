package com.pefoce.challenge_pefoce.service.transferencia;

import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.repository.TransferenciaRepository;
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
class TransferenciaQueryServiceTest {
  @Mock
  private TransferenciaRepository transferenciaRepository;
  @Mock
  private TransferenciaMapper transferenciaMapper;
  @InjectMocks
  private TransferenciaQueryService transferenciaQueryService;

  @Test
  @DisplayName("listarTodas - Deve retornar uma lista de TransferenciaDTO quando houver transferências")
  void listarTodas_DeveRetornarListaDeTransferenciaDTOs() {
    var transferenciaEntidade = new Transferencia();
    var transferenciaDTO = new TransferenciaDTO(UUID.randomUUID(), null, null, null, null, null, null);
    when(transferenciaRepository.findAll()).thenReturn(List.of(transferenciaEntidade));
    when(transferenciaMapper.toDTO(transferenciaEntidade)).thenReturn(transferenciaDTO);
    List<TransferenciaDTO> resultado = transferenciaQueryService.listarTodas();
    assertNotNull(resultado);
    assertEquals(1, resultado.size());
    assertEquals(transferenciaDTO, resultado.getFirst());
    verify(transferenciaRepository, times(1)).findAll();
    verify(transferenciaMapper, times(1)).toDTO(transferenciaEntidade);
  }

  @Test
  @DisplayName("listarTodas - Deve retornar uma lista vazia quando não houver transferências")
  void listarTodas_QuandoNaoExistemTransferencias_DeveRetornarListaVazia() {
    when(transferenciaRepository.findAll()).thenReturn(Collections.emptyList());
    List<TransferenciaDTO> resultado = transferenciaQueryService.listarTodas();
    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(transferenciaRepository, times(1)).findAll();
    verify(transferenciaMapper, never()).toDTO(any(Transferencia.class));
  }

  @Test
  @DisplayName("buscarPorId - Deve retornar TransferenciaDTO quando o ID existe")
  void buscarPorId_QuandoIdExiste_DeveRetornarTransferenciaDTO() {
    var id = UUID.randomUUID();
    var transferenciaEntidade = new Transferencia();
    var transferenciaDTO = new TransferenciaDTO(id, null, null, null, null, null, null);
    when(transferenciaRepository.findById(id)).thenReturn(Optional.of(transferenciaEntidade));
    when(transferenciaMapper.toDTO(transferenciaEntidade)).thenReturn(transferenciaDTO);
    TransferenciaDTO resultado = transferenciaQueryService.buscarPorId(id);
    assertNotNull(resultado);
    assertEquals(id, resultado.id());
    verify(transferenciaRepository, times(1)).findById(id);
    verify(transferenciaMapper, times(1)).toDTO(transferenciaEntidade);
  }

  @Test
  @DisplayName("buscarPorId - Deve lançar EntityNotFoundException quando o ID não existe")
  void buscarPorId_QuandoIdNaoExiste_DeveLancarEntityNotFoundException() {
    var id = UUID.randomUUID();
    when(transferenciaRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> transferenciaQueryService.buscarPorId(id));

    verify(transferenciaRepository, times(1)).findById(id);
    verify(transferenciaMapper, never()).toDTO(any(Transferencia.class));
  }
}