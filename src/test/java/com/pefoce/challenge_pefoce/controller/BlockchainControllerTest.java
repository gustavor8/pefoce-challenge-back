package com.pefoce.challenge_pefoce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.entity.Blockchain;
import com.pefoce.challenge_pefoce.repository.BlockchainRepository;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.service.auth.TokenService;
import com.pefoce.challenge_pefoce.service.blockchain.BlockchainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BlockchainController.class,
  excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class BlockchainControllerTest {
  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  @MockBean
  private BlockchainService blockchainService;
  @MockBean
  private BlockchainRepository blockchainRepository;
  @MockBean
  private TokenService tokenService;
  @MockBean
  private UsuarioRepository usuarioRepository; // <-- Adicione este mock!
  @Autowired
  BlockchainControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  @Test
  @DisplayName("validarCadeia - Deve retornar 200 OK com o resultado da validação")
  void validarCadeia_deveRetornar200OkComResultadoDaValidacao() throws Exception {
    var resultadoValidacao = new BlockchainValidateDTO(true, "SUCESSO: A cadeia está íntegra.");
    when(blockchainService.validarBlockchain()).thenReturn(resultadoValidacao);
    mockMvc.perform(get("/api/blockchain/validar")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.valid").value(true))
      .andExpect(jsonPath("$.message").value("SUCESSO: A cadeia está íntegra."));
  }

  @Test
  @DisplayName("listarCadeia - Deve retornar 200 OK com uma lista de blocos quando existirem")
  void listarCadeia_quandoExistemBlocos_deveRetornar200OkComLista() throws Exception {
    var bloco1 = Blockchain.builder().numeroBloco(1L).hashAtual("hash1").hashAnterior("0").build();
    var bloco2 = Blockchain.builder().numeroBloco(2L).hashAtual("hash2").hashAnterior("hash1").build();
    List<Blockchain> listaDeBlocos = List.of(bloco1, bloco2);
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(listaDeBlocos);
    mockMvc.perform(get("/api/blockchain/all")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].numeroBloco").value(1))
      .andExpect(jsonPath("$[1].numeroBloco").value(2));
  }

  @Test
  @DisplayName("listarCadeia - Deve retornar 200 OK com uma lista vazia quando não houver blocos")
  void listarCadeia_quandoNaoHaBlocos_deveRetornar200OkComListaVazia() throws Exception {
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(Collections.emptyList());
    mockMvc.perform(get("/api/blockchain/all")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("validarCadeia - Deve retornar 200 OK com resultado falso quando a cadeia for inválida")
  void validarCadeia_quandoCadeiaInvalida_deveRetornar200OkComResultadoFalso() throws Exception {
    var resultadoValidacao = new BlockchainValidateDTO(false, "ERRO DE INTEGRIDADE: O hash do Bloco Gênese #1 é inválido.");
    when(blockchainService.validarBlockchain()).thenReturn(resultadoValidacao);
    mockMvc.perform(get("/api/blockchain/validar")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.valid").value(false))
      .andExpect(jsonPath("$.message").value("ERRO DE INTEGRIDADE: O hash do Bloco Gênese #1 é inválido."));
  }
}