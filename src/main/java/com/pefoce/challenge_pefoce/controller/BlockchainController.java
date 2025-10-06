package com.pefoce.challenge_pefoce.controller;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.entity.Blockchain;
import com.pefoce.challenge_pefoce.repository.BlockchainRepository;
import com.pefoce.challenge_pefoce.service.blockchain.BlockchainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Import para documentar as respostas
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Import para agrupar as respostas
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/blockchain")
@RequiredArgsConstructor
@Tag(name = "Blockchain", description = "Endpoints para inspeção e validação da cadeia de blocos.")
public class BlockchainController {

  private final BlockchainService blockchainService;
  private final BlockchainRepository blocoBlockchainRepository;

  @GetMapping("/validar")
  @Operation(summary = "Valida a integridade da cadeia de blocos",
    description = "Recalcula o hash de cada bloco e verifica o encadeamento.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "O resultado da validação da cadeia de blocos está no corpo da resposta.")
  })
  public ResponseEntity<BlockchainValidateDTO> validarCadeia() {
    BlockchainValidateDTO blockchainValidateDTO = blockchainService.validarBlockchain();
    return ResponseEntity.ok(blockchainValidateDTO);

  }

  @GetMapping("/all")
  @Operation(summary = "Lista todos os blocos",
    description = "Retorna todos os blocos de blockchain em ordem cronológica (pelo número do bloco).")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista de blocos retornada com sucesso.")
  })
  public ResponseEntity<List<Blockchain>> listarCadeia() {
    // Busca todos os blocos ordenados pelo número, garantindo a visualização sequencial.
    List<Blockchain> blocos = blocoBlockchainRepository.findAllByOrderByNumeroBlocoAsc();
    return ResponseEntity.ok(blocos);
  }
}
