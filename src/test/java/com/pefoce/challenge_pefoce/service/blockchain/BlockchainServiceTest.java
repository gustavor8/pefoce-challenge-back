package com.pefoce.challenge_pefoce.service.blockchain;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.entity.Blockchain;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.repository.BlockchainRepository;
import com.pefoce.challenge_pefoce.util.HashUtils;
import org.junit.jupiter.api.BeforeEach; // Executar antes de cada teste.
import org.junit.jupiter.api.DisplayName; // Nomear o teste
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks; // Injetar Objetos
import org.mockito.Mock; // Criar Mock
import org.mockito.junit.jupiter.MockitoExtension; // Extensão do Mockito para JUnit 5.
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*; // Verificar os resultados
import static org.mockito.ArgumentMatchers.any; // Pro mockito aceitar qualquer tipo de dado
import static org.mockito.Mockito.*; // Importar when e verify

@ExtendWith(MockitoExtension.class)
class BlockchainServiceTest {
  @Mock
  private BlockchainRepository blockchainRepository;

  @InjectMocks
  private BlockchainService blockchainService;

  private Transferencia transferencia1;
  private Transferencia transferencia2;
  private Set<Transferencia> transacoes;

  @BeforeEach
  void setUp() {
    transferencia1 = new Transferencia();
    transferencia1.setHashTransacao("hash123");
    transferencia2 = new Transferencia();
    transferencia2.setHashTransacao("hash456");
    transacoes = new HashSet<>(Set.of(transferencia1, transferencia2));
  }

  @Test
  @DisplayName("Deve criar o bloco Gênese quando a blockchain está vazia")
  void criarNovoBloco_deveCriarBlocoGenese() {
    when(blockchainRepository.findFirstByOrderByNumeroBlocoDesc()).thenReturn(Optional.empty());
    when(blockchainRepository.save(any(Blockchain.class))).thenAnswer(invocation -> invocation.getArgument(0));
    Blockchain novoBloco = blockchainService.criarNovoBloco(transacoes);
    assertEquals(1L, novoBloco.getNumeroBloco());
    assertEquals("0", novoBloco.getHashAnterior());
    assertNotNull(novoBloco.getHashAtual());
    assertEquals(transacoes, novoBloco.getTransacoes());
    verify(blockchainRepository, times(1)).save(any(Blockchain.class));
  }

  @Test
  @DisplayName("Deve criar um novo bloco referenciando o hash do bloco anterior")
  void criarNovoBloco_deveCriarBlocoSubsequente() {
    Blockchain ultimoBloco = Blockchain.builder()
      .numeroBloco(1L)
      .hashAtual("hashDoBlocoAnterior")
      .hashAnterior("0")
      .carimboDeTempo(LocalDateTime.now())
      .transacoes(new HashSet<>())
      .build();
    when(blockchainRepository.findFirstByOrderByNumeroBlocoDesc()).thenReturn(Optional.of(ultimoBloco));
    when(blockchainRepository.save(any(Blockchain.class))).thenAnswer(invocation -> invocation.getArgument(0));
    Blockchain novoBloco = blockchainService.criarNovoBloco(transacoes);
    assertEquals(2L, novoBloco.getNumeroBloco());
    assertEquals(ultimoBloco.getHashAtual(), novoBloco.getHashAnterior());
    assertNotNull(novoBloco.getHashAtual());
    assertEquals(transacoes, novoBloco.getTransacoes());
    verify(blockchainRepository, times(1)).save(any(Blockchain.class));
  }

  @Test
  @DisplayName("Deve calcular o hash do bloco corretamente")
  void calcularHashBloco_deveRetornarHashCorreto() {
    Long numeroBloco = 1L;
    String hashAnterior = "0";
    String hashesTransacoesConcatenados = transacoes.stream()
      .map(Transferencia::getHashTransacao)
      .sorted()
      .collect(Collectors.joining());
    String dadosDoBloco = numeroBloco.toString() + hashAnterior + hashesTransacoesConcatenados;
    String hashEsperado = HashUtils.applySha256(dadosDoBloco);
    String hashCalculado = blockchainService.calcularHashBloco(numeroBloco, hashAnterior, transacoes);
    assertEquals(hashEsperado, hashCalculado);
  }
  @Test
  @DisplayName("Deve retornar válido para uma blockchain vazia")
  void validarBlockchain_quandoVazia_deveRetornarValido() {
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(Collections.emptyList());
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();
    assertTrue(resultado.valid());
    assertEquals("A cadeia de blocos é válida (está vazia).", resultado.message());
  }

  @Test
  @DisplayName("Deve retornar inválido se o hash anterior do bloco Gênese não for '0'")
  void validarBlockchain_quandoHashAnteriorDoGeneseIncorreto_deveRetornarInvalido() {
    Blockchain blocoGenese = Blockchain.builder().numeroBloco(1L).hashAnterior("hash_invalido").transacoes(transacoes).build();
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(List.of(blocoGenese));
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();
    assertFalse(resultado.valid());
    assertEquals("ERRO DE INTEGRIDADE: O hash anterior do Bloco Gênese #1 não é '0'.", resultado.message());
  }

  @Test
  @DisplayName("Deve retornar inválido se o hash atual do bloco Gênese for inválido")
  void validarBlockchain_quandoHashAtualDoGeneseInvalido_deveRetornarInvalido() {
    String hashCorreto = blockchainService.calcularHashBloco(1L, "0", transacoes);
    Blockchain blocoGenese = Blockchain.builder().numeroBloco(1L).hashAnterior("0").hashAtual("hash_adulterado").transacoes(transacoes).build();
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(List.of(blocoGenese));
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();
    assertFalse(resultado.valid());
    assertTrue(resultado.message().contains("O hash do Bloco Gênese #1 é inválido."));
  }

  @Test
  @DisplayName("Deve retornar inválido se a cadeia de hashes estiver quebrada")
  void validarBlockchain_quandoCadeiaQuebrada_deveRetornarInvalido() {
    Blockchain bloco1 = criarBlocoValido(1L, "0", transacoes);
    Blockchain bloco2 = Blockchain.builder().numeroBloco(2L).hashAnterior("hash_quebrado").hashAtual("hash_qualquer").transacoes(transacoes).build();
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(List.of(bloco1, bloco2));
BlockchainValidateDTO resultado = blockchainService.validarBlockchain();
    assertFalse(resultado.valid());
    assertTrue(resultado.message().contains("QUEBRA DE CADEIA"));
  }
  @Test
  @DisplayName("Deve retornar inválido se o conteúdo de um bloco subsequente for adulterado")
  void validarBlockchain_quandoBlocoSubsequenteAdulterado_deveRetornarInvalido() {
      Blockchain bloco1 = criarBlocoValido(1L, "0", transacoes);
      Blockchain bloco2 = Blockchain.builder().numeroBloco(2L).hashAnterior(bloco1.getHashAtual()).hashAtual("hash_adulterado").transacoes(transacoes).build();
      when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(List.of(bloco1, bloco2));
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();
    assertFalse(resultado.valid());
    assertTrue(resultado.message().contains("O hash do Bloco #2 é inválido."));
  }

  @Test
  @DisplayName("Deve retornar válido para uma blockchain íntegra e válida")
  void validarBlockchain_quandoCadeiaValida_deveRetornarValido() {
    Blockchain bloco1 = criarBlocoValido(1L, "0", transacoes);
    Blockchain bloco2 = criarBlocoValido(2L, bloco1.getHashAtual(), transacoes);
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(List.of(bloco1, bloco2));
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();
    assertTrue(resultado.valid());
    assertEquals("SUCESSO: A cadeia de blocos está íntegra e válida. Total de blocos: 2.", resultado.message());
  }
  private Blockchain criarBlocoValido(Long numeroBloco, String hashAnterior, Set<Transferencia> transacoes) {
    String hashAtual = blockchainService.calcularHashBloco(numeroBloco, hashAnterior, transacoes);
    return Blockchain.builder()
      .numeroBloco(numeroBloco)
      .hashAnterior(hashAnterior)
      .hashAtual(hashAtual)
      .transacoes(transacoes)
      .carimboDeTempo(LocalDateTime.now())
      .build();
  }
}

