package com.pefoce.challenge_pefoce.controller;

import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaCreateDTO;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.service.transferencia.TransferenciaCreateService;
import com.pefoce.challenge_pefoce.service.transferencia.TransferenciaQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transferencias")
@Tag(name = "Transferências", description = "Endpoints para gestão de transferências de vestígios")
public class TransferenciaController {
  private final TransferenciaCreateService transferenciaServiceCreate;
  private final TransferenciaQueryService transferenciaQueryService;

  public TransferenciaController(TransferenciaCreateService transferenciaService, TransferenciaQueryService transferenciaQueryService) {
    this.transferenciaServiceCreate = transferenciaService;
    this.transferenciaQueryService = transferenciaQueryService;
  }

  @PostMapping
  @Operation(
    summary = "Registrar uma nova transferência de vestígios",
    description = "Cria um registro de transferência para um ou mais vestígios, atualizando o responsável atual e o status dos itens. Requer autenticação do usuário.",
    tags = {"Transferências"},
    responses = {
      @ApiResponse(responseCode = "200", description = "Transferência registrada com sucesso.",
        content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = TransferenciaDTO.class))),
      @ApiResponse(responseCode = "400", description = "Requisição inválida (ex: dados faltando)",
        content = @Content),
      @ApiResponse(responseCode = "403", description = "Acesso negado. O usuário não possui a custódia do vestígio.",
        content = @Content),
      @ApiResponse(responseCode = "404", description = "Vestígio ou usuário de destino não encontrado.",
        content = @Content)
    }
  )
  public ResponseEntity<TransferenciaDTO> registrarTransferencia(
    @RequestBody @Valid TransferenciaCreateDTO transferenciaCreateDTO,
    @Parameter(description = "Usuário autenticado que está realizando a transferência", hidden = true)
    @AuthenticationPrincipal Usuario responsavelOrigem) {

    TransferenciaDTO transferenciaSalva = transferenciaServiceCreate.criar(transferenciaCreateDTO, responsavelOrigem);

    return ResponseEntity.ok(transferenciaSalva);
  }

  @GetMapping
  @Operation(
    summary = "Listar todas as transferências",
    description = "Retorna uma lista de todas as transferências de vestígios registradas no sistema. Requer autenticação.",
    responses = {
      @ApiResponse(responseCode = "200", description = "Lista de transferências retornada com sucesso.",
        content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = TransferenciaDTO.class))),
      @ApiResponse(responseCode = "403", description = "Acesso negado. O usuário não tem permissão.",
        content = @Content)
    }
  )
  public ResponseEntity<List<TransferenciaDTO>> listarTodasAsTransferencias() {
    List<TransferenciaDTO> transferencias = transferenciaQueryService.listarTodas();
    return ResponseEntity.ok(transferencias);
  }
}