package com.pefoce.challenge_pefoce.service.relatorio;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.dto.blockchain.CadeiaCustodiaTransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.repository.TransferenciaRepository;
import com.pefoce.challenge_pefoce.service.blockchain.BlockchainValidateTransferencia;
import com.pefoce.challenge_pefoce.service.transferencia.TransferenciaMapper;
import com.pefoce.challenge_pefoce.service.vestigio.VestigioQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class RelatorioTransferenciaVestigioServiceTest {
  @Mock
  private VestigioQueryService vestigioQueryService;
  @Mock
  private TransferenciaRepository transferenciaRepository;
  @Mock
  private TransferenciaMapper transferenciaMapper;
  @Mock
  private BlockchainValidateTransferencia validateTransferenciaService;
  @InjectMocks
  private RelatorioTransferenciaVestigioService relatorioTransferenciaVestigioService;

  @Test
  @DisplayName("Deve gerar DTO de cadeia de custódia com sucesso quando o vestígio existe")
  void gerarCadeiaDeCustodia_comVestigioExistente_deveRetornarDTOCompleto() {
    UUID vestigioId = UUID.randomUUID();
    VestigioDTO vestigioDTOMock = mock(VestigioDTO.class);
    Transferencia transferenciaEntidadeMock = mock(Transferencia.class);
    BlockchainValidateDTO statusIntegridadeMock = new BlockchainValidateDTO(true, "Cadeia íntegra.");
    TransferenciaDTO transferenciaDTOMock = mock(TransferenciaDTO.class);
    List<Transferencia> listaTransferencias = List.of(transferenciaEntidadeMock);
    when(vestigioQueryService.buscarPorId(vestigioId)).thenReturn(vestigioDTOMock);
    when(transferenciaRepository.findByVestigios_IdOrderByDataTransferenciaAsc(vestigioId)).thenReturn(listaTransferencias);
    when(validateTransferenciaService.validarHistorico(listaTransferencias)).thenReturn(statusIntegridadeMock);
    when(transferenciaMapper.toDTO(transferenciaEntidadeMock)).thenReturn(transferenciaDTOMock);
    CadeiaCustodiaTransferenciaDTO resultado = relatorioTransferenciaVestigioService.gerarCadeiaDeCustodia(vestigioId);
    assertNotNull(resultado);
    assertEquals(vestigioDTOMock, resultado.dadosDoVestigio(), "O DTO do vestígio no resultado não é o esperado.");
    assertEquals(statusIntegridadeMock, resultado.statusIntegridadeCadeia(), "O status de integridade no resultado não é o esperado.");
    assertFalse(resultado.historicoDeTransferencias().isEmpty(), "A lista de histórico de transferências não deveria estar vazia.");
    assertEquals(1, resultado.historicoDeTransferencias().size(), "A lista de histórico deveria conter 1 item.");
    assertEquals(transferenciaDTOMock, resultado.historicoDeTransferencias().get(0), "O DTO da transferência no resultado não é o esperado.");
    verify(vestigioQueryService, times(1)).buscarPorId(vestigioId);
    verify(transferenciaRepository, times(1)).findByVestigios_IdOrderByDataTransferenciaAsc(vestigioId);
    verify(validateTransferenciaService, times(1)).validarHistorico(listaTransferencias);
    verify(transferenciaMapper, times(1)).toDTO(transferenciaEntidadeMock);
  }
}
