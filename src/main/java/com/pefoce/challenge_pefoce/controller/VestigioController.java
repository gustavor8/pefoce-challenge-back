package com.pefoce.challenge_pefoce.controller;

import com.pefoce.challenge_pefoce.dto.vestigio.VestigioCreateDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioStatusResponseDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioUpdateDTO;
import com.pefoce.challenge_pefoce.service.vestigio.VestigioCreateService;
import com.pefoce.challenge_pefoce.service.vestigio.VestigioDeleteService;
import com.pefoce.challenge_pefoce.service.vestigio.VestigioQueryService;
import com.pefoce.challenge_pefoce.service.vestigio.VestigioUpdateService;
import io.swagger.v3.oas.annotations.Operation;      // Descreve um endpoint.
import io.swagger.v3.oas.annotations.media.Content;      // Descreve o conteúdo de uma resposta.
import io.swagger.v3.oas.annotations.media.Schema;       // Descreve a estrutura de um DTO.
import io.swagger.v3.oas.annotations.responses.ApiResponse;  // Descreve uma resposta HTTP.
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Agrupa múltiplas ApiResponses.
import io.swagger.v3.oas.annotations.tags.Tag;            // Agrupa endpoints em uma categoria.
import jakarta.validation.Valid; // Ativa a validação de DTOs.
import org.springframework.http.HttpStatus;       // Enum para status HTTP (ex: 201 CREATED).
import org.springframework.http.ResponseEntity; // Classe para construir respostas HTTP completas.
import org.springframework.web.bind.annotation.*; // Anotações de mapeamento de endpoints (@GetMapping, etc).

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/vestigios")

@Tag(name = "Vestígios", description = "Endpoints para gerenciamento de vestígios")
public class VestigioController {

  private final VestigioCreateService createService;
  private final VestigioQueryService queryService;
  private final VestigioUpdateService updateService;
  private final VestigioDeleteService deleteService;

  public VestigioController(VestigioCreateService createService, VestigioQueryService queryService,
                            VestigioUpdateService updateService, VestigioDeleteService deleteService) {
    this.createService = createService;
    this.queryService = queryService;
    this.updateService = updateService;
    this.deleteService = deleteService;
  }


  @Operation(summary = "Cria um novo vestígio", description = "Registra um novo vestígio no sistema com um status inicial como coletado - necessário pegar o id do usuario no bd.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Vestígio criado com sucesso",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = VestigioDTO.class))),
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
    @ApiResponse(responseCode = "404", description = "Usuário responsável não encontrado")
  })
  @PostMapping
  public ResponseEntity<VestigioDTO> criar(@RequestBody @Valid VestigioCreateDTO createDTO) {
    VestigioDTO vestigioCriado = createService.criarVestigio(createDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(vestigioCriado);
  }

  @Operation(summary = "Lista todos os vestígios", description = "Retorna uma lista de todos os vestígios cadastrados no sistema.")
  @ApiResponse(responseCode = "200", description = "Lista de vestígios retornada com sucesso")
  @GetMapping
  public ResponseEntity<List<VestigioDTO>> listarTodos() {
    List<VestigioDTO> vestigios = queryService.listarTodos();
    return ResponseEntity.ok(vestigios);
  }

  @Operation(summary = "Busca um vestígio por ID", description = "Retorna os detalhes de um vestígio específico.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Vestígio encontrado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Vestígio não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<VestigioDTO> buscarPorId(@PathVariable UUID id) {
    VestigioDTO vestigio = queryService.buscarPorId(id);
    return ResponseEntity.ok(vestigio);
  }

  @Operation(summary = "Atualiza um vestígio existente", description = "Atualiza os dados de um vestígio, como tipo, descrição e local. O status não é alterado por este endpoint.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Vestígio atualizado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
    @ApiResponse(responseCode = "404", description = "Vestígio não encontrado")
  })
  @PutMapping("/{id}")
  public ResponseEntity<VestigioDTO> atualizar(@PathVariable UUID id, @RequestBody @Valid VestigioUpdateDTO updateDTO) {
    VestigioDTO vestigioAtualizado = updateService.atualizarVestigio(id, updateDTO);
    return ResponseEntity.ok(vestigioAtualizado);
  }

  @Operation(summary = "Deleta um vestígio", description = "Remove um vestígio do sistema com base no seu ID.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Vestígio deletado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Vestígio não encontrado")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletar(@PathVariable UUID id) {
    deleteService.deletarVestigio(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Busca o status de um vestígio específico", description = "Retorna o ID e o status atual de um vestígio pelo seu UUID.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Status do vestígio encontrado com sucesso",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = VestigioStatusResponseDTO.class))),
    @ApiResponse(responseCode = "404", description = "Vestígio não encontrado")
  })
  @GetMapping("/{id}/status")
  public ResponseEntity<VestigioStatusResponseDTO> getVestigioStatus(@PathVariable UUID id) {
    VestigioStatusResponseDTO statusDTO = queryService.buscarStatusPorId(id);
    return ResponseEntity.ok(statusDTO);
  }
}