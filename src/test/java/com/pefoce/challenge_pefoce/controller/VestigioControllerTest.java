package com.pefoce.challenge_pefoce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioCreateDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioStatusResponseDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioUpdateDTO;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.service.auth.TokenService;
import com.pefoce.challenge_pefoce.service.vestigio.VestigioCreateService;
import com.pefoce.challenge_pefoce.service.vestigio.VestigioDeleteService;
import com.pefoce.challenge_pefoce.service.vestigio.VestigioQueryService;
import com.pefoce.challenge_pefoce.service.vestigio.VestigioUpdateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VestigioController.class,
  excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class VestigioControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private VestigioCreateService createService;
  @MockBean
  private VestigioQueryService queryService;
  @MockBean
  private VestigioUpdateService updateService;
  @MockBean
  private VestigioDeleteService deleteService;
  @MockBean
  private TokenService tokenService;
  @MockBean
  private UsuarioRepository usuarioRepository;

  @Test
  @DisplayName("criar - Deve retornar 201 CREATED com o vestígio criado")
  void criar_ComDadosValidos_DeveRetornar201Created() throws Exception {
    var createDTO = new VestigioCreateDTO("Celular", "iPhone 15", "Rua A, 123", OffsetDateTime.now(), UUID.randomUUID());
    var vestigioCriado = new VestigioDTO(UUID.randomUUID(), "Celular", null, null, null, null, null, null, null);
    when(createService.criarVestigio(any(VestigioCreateDTO.class))).thenReturn(vestigioCriado);
    mockMvc.perform(post("/api/vestigios")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDTO)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(vestigioCriado.id().toString()))
      .andExpect(jsonPath("$.tipo").value("Celular"));
  }

  @Test
  @DisplayName("listarTodos - Deve retornar 200 OK com a lista de vestígios")
  void listarTodos_DeveRetornar200OkComListaDeVestigios() throws Exception {
    var vestigio1 = new VestigioDTO(UUID.randomUUID(), null, null, null, null, null, null, null, null);
    var vestigio2 = new VestigioDTO(UUID.randomUUID(), null, null, null, null, null, null, null, null);
    List<VestigioDTO> lista = List.of(vestigio1, vestigio2);
    when(queryService.listarTodos()).thenReturn(lista);
    mockMvc.perform(get("/api/vestigios")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @DisplayName("buscarPorId - Deve retornar 200 OK com o vestígio encontrado")
  void buscarPorId_ComIdExistente_DeveRetornar200OkComVestigio() throws Exception {
    var id = UUID.randomUUID();
    var vestigio = new VestigioDTO(id, "Notebook", null, null, null, null, null, null, null);
    when(queryService.buscarPorId(id)).thenReturn(vestigio);
    mockMvc.perform(get("/api/vestigios/{id}", id)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.tipo").value("Notebook"));
  }

  @Test
  @DisplayName("atualizar - Deve retornar 200 OK com o vestígio atualizado")
  void atualizar_ComIdExistente_DeveRetornar200OkComVestigioAtualizado() throws Exception {
    var id = UUID.randomUUID();
    var updateDTO = new VestigioUpdateDTO("Faca", "Faca de cozinha", "Cena do crime", "EM_ANALISE");
    var vestigioAtualizado = new VestigioDTO(id, "Faca", "Faca de cozinha", null, null, "EM_ANALISE", null, null, null);
    when(updateService.atualizarVestigio(eq(id), any(VestigioUpdateDTO.class))).thenReturn(vestigioAtualizado);
    mockMvc.perform(put("/api/vestigios/{id}", id)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.tipo").value("Faca"))
      .andExpect(jsonPath("$.status").value("EM_ANALISE"));
  }

  @Test
  @DisplayName("deletar - Deve retornar 204 NO CONTENT quando deletado com sucesso")
  void deletar_ComIdExistente_DeveRetornar204NoContent() throws Exception {
    var id = UUID.randomUUID();
    doNothing().when(deleteService).deletarVestigio(id);
    mockMvc.perform(delete("/api/vestigios/{id}", id)
        .with(csrf()))
      .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("getVestigioStatus - Deve retornar 200 OK com o status do vestígio")
  void getVestigioStatus_ComIdExistente_DeveRetornar200OkComStatus() throws Exception {
    var id = UUID.randomUUID();
    var statusDTO = new VestigioStatusResponseDTO(id, "COLETADO");
    when(queryService.buscarStatusPorId(id)).thenReturn(statusDTO);
    mockMvc.perform(get("/api/vestigios/{id}/status", id)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.status").value("COLETADO"));
  }
}