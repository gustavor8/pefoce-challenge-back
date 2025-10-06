package com.pefoce.challenge_pefoce.controller;

import com.pefoce.challenge_pefoce.dto.blockchain.CadeiaCustodiaTransferenciaDTO;
import com.pefoce.challenge_pefoce.service.relatorio.PDFGenerateService;
import com.pefoce.challenge_pefoce.service.relatorio.RelatorioTransferenciaVestigioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios e auditorias")
public class RelatorioController {
  private final RelatorioTransferenciaVestigioService relatorioService;
  private final PDFGenerateService pdfService;

  public RelatorioController(RelatorioTransferenciaVestigioService relatorioService, PDFGenerateService pdfService) {
    this.relatorioService = relatorioService;
    this.pdfService = pdfService;
  }

  @GetMapping("/cadeia-custodia/{vestigioId}")
  @Operation(summary = "Gera o relatório da Cadeia de Custódia de transferência de um vestígio validando no blockchain")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Vestígio não encontrado com o ID fornecido")
  })
  public ResponseEntity<CadeiaCustodiaTransferenciaDTO> gerarRelatorioCadeiaCustodia(@PathVariable UUID vestigioId) {
    CadeiaCustodiaTransferenciaDTO relatorio = relatorioService.gerarCadeiaDeCustodia(vestigioId);
    return ResponseEntity.ok(relatorio);
  }

  @GetMapping(value = "/cadeia-custodia/{vestigioId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  @Operation(summary = "Exporta o relatório da Cadeia de Custódia para PDF")
  public ResponseEntity<byte[]> exportarCadeiaCustodiaPdf(@PathVariable UUID vestigioId) {
    CadeiaCustodiaTransferenciaDTO relatorioDados = relatorioService.gerarCadeiaDeCustodia(vestigioId);

    byte[] pdfBytes = pdfService.gerarPdfCadeiaCustodia(relatorioDados);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-cadeia-custodia-" + vestigioId + ".pdf");

    return ResponseEntity.ok()
      .headers(headers)
      .contentType(MediaType.APPLICATION_PDF)
      .body(pdfBytes);
  }
}