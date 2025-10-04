package com.pefoce.challenge_pefoce.controller;

import com.pefoce.challenge_pefoce.dto.blockchain.CadeiaCustodiaTransferenciaDTO;

import com.pefoce.challenge_pefoce.service.relatorio.RelatorioTransferenciaVestigioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;  // Anotação para extrair valores da URL.
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios e auditorias")
public class RelatorioController {
  private final RelatorioTransferenciaVestigioService relatorioService;

  public RelatorioController(RelatorioTransferenciaVestigioService relatorioService) {
    this.relatorioService = relatorioService;
  }

  @GetMapping("/cadeia-custodia/{vestigioId}")
  @Operation(summary = "Gera o relatório da Cadeia de Custódia de um vestígio validando no blockchain")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Vestígio não encontrado com o ID fornecido")
  })
  public ResponseEntity<CadeiaCustodiaTransferenciaDTO> gerarRelatorioCadeiaCustodia(@PathVariable UUID vestigioId) {
    CadeiaCustodiaTransferenciaDTO relatorio = relatorioService.gerarCadeiaDeCustodia(vestigioId);
    return ResponseEntity.ok(relatorio);
  }
}