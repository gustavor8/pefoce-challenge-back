package com.pefoce.challenge_pefoce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.dto.blockchain.CadeiaCustodiaTransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.service.auth.TokenService;
import com.pefoce.challenge_pefoce.service.relatorio.PDFGenerateService;
import com.pefoce.challenge_pefoce.service.relatorio.RelatorioTransferenciaVestigioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RelatorioController.class,
  excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class RelatorioControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private RelatorioTransferenciaVestigioService relatorioService;
  @MockBean
  private PDFGenerateService pdfService;
  @MockBean
  private TokenService tokenService;
  @MockBean
  private UsuarioRepository usuarioRepository;

  @Test
  @DisplayName("gerarRelatorioCadeiaCustodia - Deve retornar 200 OK com o relatório em JSON")
  void gerarRelatorioCadeiaCustodia_deveRetornar200OkComRelatorio() throws Exception {
    var vestigioId = UUID.randomUUID();
    var vestigioDTO = new VestigioDTO(vestigioId, null, null, null, null, null, null, null, null);
    var statusDTO = new BlockchainValidateDTO(true, "Cadeia íntegra");
    List<TransferenciaDTO> historico = Collections.emptyList();
    var relatorioDTO = new CadeiaCustodiaTransferenciaDTO(vestigioDTO, statusDTO, historico);
    when(relatorioService.gerarCadeiaDeCustodia(vestigioId)).thenReturn(relatorioDTO);
    mockMvc.perform(get("/api/relatorios/cadeia-custodia/{vestigioId}", vestigioId)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.dadosDoVestigio.id").value(vestigioId.toString()))
      .andExpect(jsonPath("$.statusIntegridadeCadeia.valid").value(true));
  }

  @Test
  @DisplayName("exportarCadeiaCustodiaPdf - Deve retornar 200 OK com o arquivo PDF")
  void exportarCadeiaCustodiaPdf_deveRetornar200OkComPdf() throws Exception {
    var vestigioId = UUID.randomUUID();
    byte[] pdfBytes = "Conteúdo do PDF de teste".getBytes();
    var vestigioDTO = new VestigioDTO(vestigioId, null, null, null, null, null, null, null, null);
    var statusDTO = new BlockchainValidateDTO(true, "Cadeia íntegra");
    List<TransferenciaDTO> historico = Collections.emptyList();
    var relatorioDados = new CadeiaCustodiaTransferenciaDTO(vestigioDTO, statusDTO, historico);
    when(relatorioService.gerarCadeiaDeCustodia(vestigioId)).thenReturn(relatorioDados);
    when(pdfService.gerarPdfCadeiaCustodia(any(CadeiaCustodiaTransferenciaDTO.class))).thenReturn(pdfBytes);
    mockMvc.perform(get("/api/relatorios/cadeia-custodia/{vestigioId}/pdf", vestigioId))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_PDF))
      .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-cadeia-custodia-" + vestigioId + ".pdf"))
      .andExpect(content().bytes(pdfBytes));
  }
}