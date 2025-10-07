package com.pefoce.challenge_pefoce.service.blockchain;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.entity.Blockchain;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.repository.BlockchainRepository;
import com.pefoce.challenge_pefoce.util.HashUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat; //Par usar sem a classe
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainServiceTest {

  @Mock
  private BlockchainRepository blockchainRepository;

  @InjectMocks
  private BlockchainService blockchainService;

  @Test
  @DisplayName("Deve criar o Bloco Gênese quando a blockchain estiver vazia")
  void shouldCreateGenesisBlock_whenChainIsEmpty() {
    Set<Transferencia> transacoes = new HashSet<>(Set.of(
      Transferencia.builder().hashTransacao("hash1").build(),
      Transferencia.builder().hashTransacao("hash2").build()
    ));
    when(blockchainRepository.findFirstByOrderByNumeroBlocoDesc()).thenReturn(Optional.empty());
    when(blockchainRepository.save(any(Blockchain.class))).thenAnswer(invocation -> invocation.getArgument(0));
    Blockchain novoBloco = blockchainService.criarNovoBloco(transacoes);
    assertThat(novoBloco).isNotNull();
    assertThat(novoBloco.getNumeroBloco()).isEqualTo(1L);
    assertThat(novoBloco.getHashAnterior()).isEqualTo("0");
    assertThat(novoBloco.getHashAtual()).isNotNull().isNotBlank();
    assertThat(novoBloco.getCarimboDeTempo()).isNotNull();
    assertThat(novoBloco.getTransacoes()).isEqualTo(transacoes);
    novoBloco.getTransacoes().forEach(t -> assertThat(t.getBlockchain()).isEqualTo(novoBloco));
    verify(blockchainRepository, times(1)).save(any(Blockchain.class));
  }

  @Test
  @DisplayName("Deve criar um novo bloco referenciando o hash do bloco anterior")
  void shouldCreateNextBlock_whenChainHasExistingBlocks() {
    Blockchain ultimoBloco = Blockchain.builder()
      .numeroBloco(5L)
      .hashAtual("hash_do_bloco_5")
      .build();
    Set<Transferencia> transacoes = new HashSet<>(Set.of(
      Transferencia.builder().hashTransacao("hashA").build()
    ));
    when(blockchainRepository.findFirstByOrderByNumeroBlocoDesc()).thenReturn(Optional.of(ultimoBloco));
    when(blockchainRepository.save(any(Blockchain.class))).thenAnswer(invocation -> invocation.getArgument(0));
    Blockchain novoBloco = blockchainService.criarNovoBloco(transacoes);
    assertThat(novoBloco).isNotNull();
    assertThat(novoBloco.getNumeroBloco()).isEqualTo(6L);
    assertThat(novoBloco.getHashAnterior()).isEqualTo("hash_do_bloco_5");
    verify(blockchainRepository, times(1)).save(any(Blockchain.class));
  }

  @Test
  @DisplayName("Deve calcular o hash de um bloco corretamente, ordenando os hashes das transações")
  void shouldCalculateBlockHashCorrectly() {
    Long numeroBloco = 10L;
    String hashAnterior = "hash_anterior_123";
    Set<Transferencia> transacoes = new HashSet<>(Set.of(
      Transferencia.builder().hashTransacao("hash_transacao_C").build(),
      Transferencia.builder().hashTransacao("hash_transacao_A").build(),
      Transferencia.builder().hashTransacao("hash_transacao_B").build()
    ));
    String hashesConcatenadosOrdenados = "hash_transacao_Ahash_transacao_Bhash_transacao_C";
    String dadosEsperadosParaHash = numeroBloco.toString() + hashAnterior + hashesConcatenadosOrdenados;
    String hashEsperado = HashUtils.applySha256(dadosEsperadosParaHash);
    String hashCalculado = blockchainService.calcularHashBloco(numeroBloco, hashAnterior, transacoes);

    assertThat(hashCalculado).isEqualTo(hashEsperado);
  }

  @Test
  @DisplayName("Deve retornar válido para uma blockchain vazia")
  void shouldReturnValid_whenChainIsEmpty() {
    // Arrange
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(Collections.emptyList());

    // Act
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();

    // Assert
    assertThat(resultado.valid()).isTrue();
    assertThat(resultado.message()).contains("A cadeia de blocos é válida (está vazia)");
  }

  @Test
  @DisplayName("Deve retornar válido para uma blockchain com múltiplos blocos íntegros")
  void shouldReturnValid_whenChainIsCorrect() {
    // Arrange
    List<Blockchain> blocos = criarCadeiaDeBlocosValida(3);
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(blocos);

    // Act
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();

    // Assert
    assertThat(resultado.valid()).isTrue();
    assertThat(resultado.message()).isEqualTo("SUCESSO: A cadeia de blocos está íntegra e válida. Total de blocos: 3.");
  }

  @Test
  @DisplayName("Deve retornar inválido se o hash anterior do Bloco Gênese não for '0'")
  void shouldReturnInvalid_whenGenesisHashAnteriorIsNotZero() {
    // Arrange
    List<Blockchain> blocos = criarCadeiaDeBlocosValida(1);
    blocos.get(0).setHashAnterior("hash_errado_no_genese");
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(blocos);

    // Act
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();

    // Assert
    assertThat(resultado.valid()).isFalse();
    assertThat(resultado.message()).contains("ERRO DE INTEGRIDADE: O hash anterior do Bloco Gênese #1 não é '0'.");
  }

  @Test
  @DisplayName("Deve retornar inválido se o conteúdo do Bloco Gênese for adulterado")
  void shouldReturnInvalid_whenGenesisContentIsTampered() {
    // Arrange
    List<Blockchain> blocos = criarCadeiaDeBlocosValida(1);
    blocos.get(0).setHashAtual("hash_adulterado");
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(blocos);

    // Act
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();

    // Assert
    assertThat(resultado.valid()).isFalse();
    assertThat(resultado.message()).contains("O conteúdo deste bloco foi adulterado");
  }

  @Test
  @DisplayName("Deve retornar inválido se a cadeia de hashes estiver quebrada entre blocos")
  void shouldReturnInvalid_whenChainIsBroken() {
    // Arrange
    List<Blockchain> blocos = criarCadeiaDeBlocosValida(3);
    blocos.get(2).setHashAnterior("hash_quebrado_propositalmente");
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(blocos);

    // Act
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();

    // Assert
    assertThat(resultado.valid()).isFalse();
    assertThat(resultado.message()).contains("QUEBRA DE CADEIA: O hash anterior do Bloco #3 está incorreto.");
  }

  @Test
  @DisplayName("Deve retornar inválido se o conteúdo de um bloco intermediário for adulterado")
  void shouldReturnInvalid_whenMiddleBlockContentIsTampered() {
    // Arrange
    List<Blockchain> blocos = criarCadeiaDeBlocosValida(3);
    blocos.get(1).setHashAtual("hash_adulterado_no_meio");
    when(blockchainRepository.findAllByOrderByNumeroBlocoAsc()).thenReturn(blocos);

    // Act
    BlockchainValidateDTO resultado = blockchainService.validarBlockchain();

    // Assert
    assertThat(resultado.valid()).isFalse();
    // CORREÇÃO: Removidas as reticências "..." no final da string esperada.
    // A verificação agora procura por uma parte da mensagem que realmente existe.
    assertThat(resultado.message()).contains("ERRO DE INTEGRIDADE: O hash do Bloco #2 é inválido.");
  }

  // --- Métodos de Apoio (Helpers) ---
  private List<Blockchain> criarCadeiaDeBlocosValida(int quantidadeDeBlocos) {
    List<Blockchain> blocos = new ArrayList<>();
    String hashAnterior = "0";
    for (long i = 1; i <= quantidadeDeBlocos; i++) {
      Set<Transferencia> transacoes = Set.of(
        Transferencia.builder().hashTransacao("hash_transacao_" + i).build()
      );
      String hashAtual = blockchainService.calcularHashBloco(i, hashAnterior, transacoes);
      Blockchain bloco = Blockchain.builder()
        .numeroBloco(i)
        .hashAnterior(hashAnterior)
        .hashAtual(hashAtual)
        .transacoes(transacoes)
        .carimboDeTempo(LocalDateTime.now())
        .build();
      blocos.add(bloco);
      hashAnterior = hashAtual;
    }
    return blocos;
  }
}

