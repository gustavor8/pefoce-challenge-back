package com.pefoce.challenge_pefoce.service.blockchain;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.entity.Blockchain;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.util.HashUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainValidateTransferenciaTest {

  @Mock
  private BlockchainService blockchainService;

  @InjectMocks
  private BlockchainValidateTransferencia blockchainValidateTransferencia;
  private MockedStatic<HashUtils> mockedHashUtils;

  @BeforeEach
  void setUp() {
    mockedHashUtils = Mockito.mockStatic(HashUtils.class);
  }

  @AfterEach
  void tearDown() {
    mockedHashUtils.close();
  }


  @Test
  @DisplayName("Deve retornar válido quando o histórico de transferências está íntegro")
  void shouldReturnValid_whenHistoryIsCorrect() {
    UUID transferenciaId = UUID.randomUUID();
    Blockchain bloco = Blockchain.builder().numeroBloco(1L).hashAnterior("0").hashAtual("hash_bloco_valido").build();
    Transferencia transferencia = Transferencia.builder().id(transferenciaId).hashTransacao("hash_trans_valido").blockchain(bloco).build();
    bloco.setTransacoes(Set.of(transferencia));

    mockedHashUtils.when(() -> HashUtils.calculateTransferHash(transferencia)).thenReturn("hash_trans_valido");
    when(blockchainService.calcularHashBloco(bloco.getNumeroBloco(), bloco.getHashAnterior(), bloco.getTransacoes()))
      .thenReturn("hash_bloco_valido");

    BlockchainValidateDTO resultado = blockchainValidateTransferencia.validarHistorico(List.of(transferencia));

    assertThat(resultado.valid()).isTrue();
    assertThat(resultado.message()).isEqualTo("A cadeia de custódia para este vestígio está íntegra.");
  }

  @Test
  @DisplayName("Deve retornar inválido se o hash de uma transferência for adulterado")
  void shouldReturnInvalid_whenTransferenciaIsTampered() {
    UUID transferenciaId = UUID.randomUUID();
    Transferencia transferenciaAdulterada = Transferencia.builder().id(transferenciaId).hashTransacao("hash_salvo_no_banco").build();
    mockedHashUtils.when(() -> HashUtils.calculateTransferHash(transferenciaAdulterada)).thenReturn("hash_calculado_diferente");

    BlockchainValidateDTO resultado = blockchainValidateTransferencia.validarHistorico(List.of(transferenciaAdulterada));

    assertThat(resultado.valid()).isFalse();
    assertThat(resultado.message()).isEqualTo("ERRO DE INTEGRIDADE: Os dados da Transferência ID " + transferenciaId + " foram adulterados.");
  }

  @Test
  @DisplayName("Deve retornar inválido se o bloco que contém a transferência for adulterado")
  void shouldReturnInvalid_whenBlockIsTampered() {
    UUID transferenciaId = UUID.randomUUID();
    Blockchain blocoAdulterado = Blockchain.builder().numeroBloco(2L).hashAnterior("hash_anterior").hashAtual("hash_bloco_salvo_no_banco").build();
    Transferencia transferencia = Transferencia.builder().id(transferenciaId).hashTransacao("hash_trans_valido").blockchain(blocoAdulterado).build();
    blocoAdulterado.setTransacoes(Set.of(transferencia));

    mockedHashUtils.when(() -> HashUtils.calculateTransferHash(transferencia)).thenReturn("hash_trans_valido");
    when(blockchainService.calcularHashBloco(any(), any(), any())).thenReturn("hash_bloco_calculado_diferente");

    BlockchainValidateDTO resultado = blockchainValidateTransferencia.validarHistorico(List.of(transferencia));

    assertThat(resultado.valid()).isFalse();
    assertThat(resultado.message()).isEqualTo("ERRO DE INTEGRIDADE: O Bloco #2, que armazena a Transferência ID " + transferenciaId + ", foi adulterado.");
  }

  @Test
  @DisplayName("Deve retornar válido para uma lista de transferências vazia")
  void shouldReturnValid_whenListOfTransferenciasIsEmpty() {
    BlockchainValidateDTO resultado = blockchainValidateTransferencia.validarHistorico(Collections.emptyList());

    assertThat(resultado.valid()).isTrue();
    assertThat(resultado.message()).isEqualTo("A cadeia de custódia para este vestígio está íntegra.");
  }

  @Test
  @DisplayName("Deve retornar válido se uma transferência íntegra ainda não estiver em um bloco")
  void shouldReturnValid_whenTransferenciaHasNoBlock() {
    UUID transferenciaId = UUID.randomUUID();
    Transferencia transferenciaSemBloco = Transferencia.builder().id(transferenciaId).hashTransacao("hash_valido").blockchain(null).build();
    mockedHashUtils.when(() -> HashUtils.calculateTransferHash(transferenciaSemBloco)).thenReturn("hash_valido");

    BlockchainValidateDTO resultado = blockchainValidateTransferencia.validarHistorico(List.of(transferenciaSemBloco));

    assertThat(resultado.valid()).isTrue();
    assertThat(resultado.message()).isEqualTo("A cadeia de custódia para este vestígio está íntegra.");
    verify(blockchainService, never()).calcularHashBloco(any(), any(), any());
  }
}

