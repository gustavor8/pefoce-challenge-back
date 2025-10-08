package com.pefoce.challenge_pefoce.service.blockchain;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.entity.Blockchain;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.util.HashUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Extensão do Mockito para JUnit 5.
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlockchainValidateTransferenciaTest {
  @Mock
  private BlockchainService blockchainService;
 @InjectMocks
  private BlockchainValidateTransferencia blockchainValidateTransferencia;
  private Transferencia transferenciaValida;
  private Blockchain blocoValido;
  @BeforeEach
  void setUp() {
    Set<Transferencia> transacoesDoBloco = new HashSet<>();
    Usuario responsavelOrigem = new Usuario();
    responsavelOrigem.setId(UUID.randomUUID());
    Usuario responsavelDestino = new Usuario();
    responsavelDestino.setId(UUID.randomUUID());
    transferenciaValida = new Transferencia();
    transferenciaValida.setId(UUID.randomUUID());
    transferenciaValida.setResponsavelOrigem(responsavelOrigem);
    transferenciaValida.setResponsavelDestino(responsavelDestino);
    transferenciaValida.setDataTransferencia(OffsetDateTime.now());
    blocoValido = Blockchain.builder()
      .numeroBloco(1L)
      .hashAnterior("0")
      .carimboDeTempo(LocalDateTime.now())
      .transacoes(transacoesDoBloco)
      .build();
    transferenciaValida.setBlockchain(blocoValido);
    transferenciaValida.setHashTransacao(HashUtils.calculateTransferHash(transferenciaValida));
    transacoesDoBloco.add(transferenciaValida);
    String hashesTransacoesConcatenados = transacoesDoBloco.stream()
      .map(Transferencia::getHashTransacao)
      .sorted()
      .collect(Collectors.joining());
    String dadosDoBloco = blocoValido.getNumeroBloco().toString() + blocoValido.getHashAnterior() + hashesTransacoesConcatenados;
    blocoValido.setHashAtual(HashUtils.applySha256(dadosDoBloco));
  }

  @Test
  @DisplayName("Deve retornar válido quando a transferência e seu bloco estão íntegros")
  void validarHistorico_quandoTudoIntegro_deveRetornarValido() {
    when(blockchainService.calcularHashBloco(blocoValido.getNumeroBloco(), blocoValido.getHashAnterior(), blocoValido.getTransacoes()))
      .thenReturn(blocoValido.getHashAtual());
    BlockchainValidateDTO resultado = blockchainValidateTransferencia.validarHistorico(List.of(transferenciaValida));
    assertTrue(resultado.valid());
    assertEquals("A cadeia de custódia para este vestígio está íntegra.", resultado.message());
  }

  @Test
  @DisplayName("Deve retornar inválido se o hash da transferência for inconsistente")
  void validarHistorico_quandoHashTransferenciaAdulterado_deveRetornarInvalido() {
    transferenciaValida.setHashTransacao("hash_adulterado");
    BlockchainValidateDTO resultado = blockchainValidateTransferencia.validarHistorico(List.of(transferenciaValida));
    assertFalse(resultado.valid());
    assertEquals(String.format("ERRO DE INTEGRIDADE: Os dados da Transferência ID %s foram adulterados.", transferenciaValida.getId()), resultado.message());
  }

  @Test
  @DisplayName("Deve retornar inválido se o hash do bloco da transferência for inconsistente")
  void validarHistorico_quandoHashBlocoAdulterado_deveRetornarInvalido() {
    when(blockchainService.calcularHashBloco(blocoValido.getNumeroBloco(), blocoValido.getHashAnterior(), blocoValido.getTransacoes()))
      .thenReturn("hash_calculado_diferente");
    BlockchainValidateDTO resultado = blockchainValidateTransferencia.validarHistorico(List.of(transferenciaValida));
    assertFalse(resultado.valid());
    assertEquals(String.format("ERRO DE INTEGRIDADE: O Bloco #%d, que armazena a Transferência ID %s, foi adulterado.", blocoValido.getNumeroBloco(), transferenciaValida.getId()), resultado.message());
  }

  @Test
  @DisplayName("Deve retornar válido para uma transferência íntegra sem bloco associado")
  void validarHistorico_quandoTransferenciaSemBloco_deveRetornarValido() {
    transferenciaValida.setBlockchain(null);
    transferenciaValida.setHashTransacao(HashUtils.calculateTransferHash(transferenciaValida));
    BlockchainValidateDTO resultado = blockchainValidateTransferencia.validarHistorico(List.of(transferenciaValida));
    assertTrue(resultado.valid());
    assertEquals("A cadeia de custódia para este vestígio está íntegra.", resultado.message());
  }

  @Test
  @DisplayName("Deve retornar válido para uma lista de transferências vazia")
  void validarHistorico_quandoListaVazia_deveRetornarValido() {
    BlockchainValidateDTO resultado = blockchainValidateTransferencia.validarHistorico(Collections.emptyList());
    assertTrue(resultado.valid());
    assertEquals("A cadeia de custódia para este vestígio está íntegra.", resultado.message());
  }
}

