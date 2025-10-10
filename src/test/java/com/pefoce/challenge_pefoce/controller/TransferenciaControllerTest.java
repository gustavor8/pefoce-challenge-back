package com.pefoce.challenge_pefoce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaCreateDTO;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.usuario.UsuarioResponsavelDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.service.auth.TokenService;
import com.pefoce.challenge_pefoce.service.transferencia.TransferenciaCreateService;
import com.pefoce.challenge_pefoce.service.transferencia.TransferenciaQueryService;
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
import java.util.Set;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransferenciaController.class,
  excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class TransferenciaControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private TransferenciaCreateService transferenciaCreateService;
  @MockBean
  private TransferenciaQueryService transferenciaQueryService;
  @MockBean
  private TokenService tokenService;
  @MockBean
  private UsuarioRepository usuarioRepository;

  @Test
  @DisplayName("listarTodasAsTransferencias - Deve retornar 200 OK com uma lista de transferências")
  void listarTodasAsTransferencias_deveRetornar200OkComListaDeTransferencias() throws Exception {
    var responsavelDTO = new UsuarioResponsavelDTO(UUID.randomUUID(), "Usuario Origem");
    var transferenciaDTO = new TransferenciaDTO(UUID.randomUUID(), null, null, null, null, responsavelDTO, null);
    List<TransferenciaDTO> listaDeTransferencias = List.of(transferenciaDTO);
    when(transferenciaQueryService.listarTodas()).thenReturn(listaDeTransferencias);
    mockMvc.perform(get("/api/transferencias")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].id").value(transferenciaDTO.id().toString()));
  }

  @Test
  @DisplayName("registrarTransferencia - Deve retornar 200 OK com a transferência criada")
  void registrarTransferencia_deveRetornar200OkComTransferenciaCriada() throws Exception {
    var responsavelOrigem = Usuario.builder()
      .id(UUID.randomUUID())
      .username("perito.teste")
      .password("senha")
      .build();
    var responsavelDestinoId = UUID.randomUUID();
    var vestigioId = UUID.randomUUID();
    var vestigiosIds = Set.of(vestigioId);
    var transferenciaCreateDTO = new TransferenciaCreateDTO(vestigiosIds, responsavelDestinoId, "Observação de teste");
    var responsavelOrigemDTO = new UsuarioResponsavelDTO(responsavelOrigem.getId(), responsavelOrigem.getUsername());
    var transferenciaSalvaDTO = new TransferenciaDTO(UUID.randomUUID(), "motivo", OffsetDateTime.now(), "hash", null, responsavelOrigemDTO, null);
    when(transferenciaCreateService.criar(any(TransferenciaCreateDTO.class), any(Usuario.class))).thenReturn(transferenciaSalvaDTO);
    mockMvc.perform(post("/api/transferencias")
        .with(csrf())
        .with(user(responsavelOrigem))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(transferenciaCreateDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(transferenciaSalvaDTO.id().toString()))
      .andExpect(jsonPath("$.responsavelOrigem.id").value(responsavelOrigem.getId().toString()));
  }
}