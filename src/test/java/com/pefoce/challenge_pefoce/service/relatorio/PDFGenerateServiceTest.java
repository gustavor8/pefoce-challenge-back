package com.pefoce.challenge_pefoce.service.relatorio;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.dto.blockchain.CadeiaCustodiaTransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.usuario.UsuarioResponsavelDTO;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PDFGenerateServiceTest {
  private PDFGenerateService pdfGenerateService;
  @BeforeEach
  void setUp() {
    pdfGenerateService = new PDFGenerateService();
  }
  @Test
  @DisplayName("Deve gerar PDF com sucesso quando todos os dados estão completos e a cadeia está íntegra")
  void gerarPdfCadeiaCustodia_comDadosCompletos_deveGerarPdfNaoVazio() {
    UsuarioResponsavelDTO usuario1 = Mockito.mock(UsuarioResponsavelDTO.class);
    when(usuario1.nome()).thenReturn("Carlos Andrade");
    UsuarioResponsavelDTO usuario2 = Mockito.mock(UsuarioResponsavelDTO.class);
    when(usuario2.nome()).thenReturn("Beatriz Lima");
    VestigioDTO vestigio = Mockito.mock(VestigioDTO.class);
    when(vestigio.id()).thenReturn(UUID.randomUUID());
    when(vestigio.tipo()).thenReturn("Celular");
    when(vestigio.descricao()).thenReturn("iPhone 15 Pro");
    when(vestigio.localColeta()).thenReturn("Cena do Crime A");
    when(vestigio.responsavelAtual()).thenReturn(usuario2);
    BlockchainValidateDTO status = new BlockchainValidateDTO(true, "A cadeia de blocos está íntegra e válida.");
    TransferenciaDTO transferencia1 = Mockito.mock(TransferenciaDTO.class);
    when(transferencia1.dataTransferencia()).thenReturn(OffsetDateTime.now().minusDays(1));
    when(transferencia1.responsavelOrigem()).thenReturn(usuario1);
    when(transferencia1.responsavelDestino()).thenReturn(usuario2);
    when(transferencia1.motivo()).thenReturn("Análise laboratorial");
    TransferenciaDTO transferencia2 = Mockito.mock(TransferenciaDTO.class);
    when(transferencia2.dataTransferencia()).thenReturn(OffsetDateTime.now());
    when(transferencia2.responsavelOrigem()).thenReturn(usuario2);
    when(transferencia2.responsavelDestino()).thenReturn(usuario1);
    when(transferencia2.motivo()).thenReturn(null);
    List<TransferenciaDTO> historico = List.of(transferencia1, transferencia2);
    CadeiaCustodiaTransferenciaDTO dadosRelatorio = new CadeiaCustodiaTransferenciaDTO(vestigio, status, historico);
    byte[] resultado = pdfGenerateService.gerarPdfCadeiaCustodia(dadosRelatorio);
    assertNotNull(resultado, "O array de bytes do PDF não deveria ser nulo.");
    assertTrue(resultado.length > 0, "O array de bytes do PDF não deveria estar vazio.");
  }

  @Test
  @DisplayName("Deve gerar PDF com sucesso quando a cadeia de custódia está corrompida")
  void gerarPdfCadeiaCustodia_comCadeiaCorrompida_deveGerarPdfNaoVazio() {
    UsuarioResponsavelDTO usuario1 = Mockito.mock(UsuarioResponsavelDTO.class);
    when(usuario1.nome()).thenReturn("Carlos Andrade");
    VestigioDTO vestigio = Mockito.mock(VestigioDTO.class);
    when(vestigio.id()).thenReturn(UUID.randomUUID());
    when(vestigio.tipo()).thenReturn("Notebook");
    when(vestigio.descricao()).thenReturn("Dell XPS");
    when(vestigio.localColeta()).thenReturn("Cena do Crime B");
    when(vestigio.responsavelAtual()).thenReturn(usuario1);
    BlockchainValidateDTO statusCorrompido = new BlockchainValidateDTO(false, "ERRO DE INTEGRIDADE: O hash do Bloco #2 é inválido.");
    TransferenciaDTO transferencia = Mockito.mock(TransferenciaDTO.class);
    when(transferencia.dataTransferencia()).thenReturn(OffsetDateTime.now());
    when(transferencia.responsavelOrigem()).thenReturn(Mockito.mock(UsuarioResponsavelDTO.class));
    when(transferencia.responsavelOrigem().nome()).thenReturn("Ana Souza");
    when(transferencia.responsavelDestino()).thenReturn(usuario1);
    when(transferencia.motivo()).thenReturn("Coleta inicial");
    CadeiaCustodiaTransferenciaDTO dadosRelatorio = new CadeiaCustodiaTransferenciaDTO(vestigio, statusCorrompido, List.of(transferencia));
    byte[] resultado = pdfGenerateService.gerarPdfCadeiaCustodia(dadosRelatorio);
    assertNotNull(resultado, "O PDF deveria ser gerado mesmo com a cadeia corrompida.");
    assertTrue(resultado.length > 0, "O array de bytes do PDF não deveria estar vazio.");
  }

  @Test
  @DisplayName("Deve gerar PDF com sucesso quando não há histórico de transferências")
  void gerarPdfCadeiaCustodia_semHistoricoDeTransferencias_deveGerarPdfNaoVazio() {
    UsuarioResponsavelDTO usuario1 = Mockito.mock(UsuarioResponsavelDTO.class);
    when(usuario1.nome()).thenReturn("Mariana Costa");
    VestigioDTO vestigio = Mockito.mock(VestigioDTO.class);
    when(vestigio.id()).thenReturn(UUID.randomUUID());
    when(vestigio.tipo()).thenReturn("Faca");
    when(vestigio.descricao()).thenReturn("Faca de cozinha");
    when(vestigio.localColeta()).thenReturn("Cena do Crime C");
    when(vestigio.responsavelAtual()).thenReturn(usuario1);
    BlockchainValidateDTO status = new BlockchainValidateDTO(true, "Cadeia íntegra.");
    List<TransferenciaDTO> historicoVazio = Collections.emptyList();
    CadeiaCustodiaTransferenciaDTO dadosRelatorio = new CadeiaCustodiaTransferenciaDTO(vestigio, status, historicoVazio);
    byte[] resultado = pdfGenerateService.gerarPdfCadeiaCustodia(dadosRelatorio);
    assertNotNull(resultado, "O PDF deveria ser gerado mesmo sem histórico de transferências.");
    assertTrue(resultado.length > 0, "O array de bytes do PDF não deveria estar vazio.");
  }
}

