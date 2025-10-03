package com.pefoce.challenge_pefoce.service.util;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.entity.BlocoBlockchain;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.repository.BlocoBlockchainRepository;
import com.pefoce.challenge_pefoce.util.HashUtils; // calcular o hash SHA-256.
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BlockchainService {
  private final BlocoBlockchainRepository blocoBlockchainRepository;

  public BlockchainService(BlocoBlockchainRepository blocoBlockchainRepository) {
    this.blocoBlockchainRepository = blocoBlockchainRepository;
  }

  public BlocoBlockchain criarNovoBloco(Set<Transferencia> transacoes) {
    Optional<BlocoBlockchain> ultimoBloco = blocoBlockchainRepository.findFirstByOrderByNumeroBlocoDesc();
    Long numeroBloco = ultimoBloco.map(b -> b.getNumeroBloco() + 1).orElse(1L);
    String hashAnterior = ultimoBloco.map(BlocoBlockchain::getHashAtual).orElse("0");
    String hashAtual = calcularHashBloco(numeroBloco, hashAnterior, transacoes);

    BlocoBlockchain novoBloco = BlocoBlockchain.builder()
      .numeroBloco(numeroBloco)
      .hashAnterior(hashAnterior)
      .hashAtual(hashAtual)
      .transacoes(transacoes)
      .carimboDeTempo(LocalDateTime.now())
      .build();

    return blocoBlockchainRepository.save(novoBloco);
  }

  private String calcularHashBloco(Long numeroBloco, String hashAnterior, Set<Transferencia> transacoes) {
    String dadosDoBloco = numeroBloco.toString() + hashAnterior + transacoes.stream()
      .map(Transferencia::getHashTransacao)
      .reduce("", String::concat);

    return HashUtils.applySha256(dadosDoBloco);
  }

  public BlockchainValidateDTO validarBlockchain() {
    List<BlocoBlockchain> blocos = blocoBlockchainRepository.findAllByOrderByNumeroBlocoAsc();

    if (blocos.size() <= 1) {
      String message = "A cadeia de blocos é válida (contém " + blocos.size() + " bloco(s)).";
      return new BlockchainValidateDTO(true, message);
    }

    for (int i = 1; i < blocos.size(); i++) {
      BlocoBlockchain blocoAtual = blocos.get(i);
      BlocoBlockchain blocoAnterior = blocos.get(i - 1);

      // 1. Recalcula o hash do bloco atual
      String hashCalculado = calcularHashBloco(
        blocoAtual.getNumeroBloco(),
        blocoAtual.getHashAnterior(),
        blocoAtual.getTransacoes()
      );

      // VERIFICAÇÃO 1: Integridade do Hash Atual
      if (!hashCalculado.equals(blocoAtual.getHashAtual())) {
        String message = String.format(
          "ERRO DE INTEGRIDADE: O hash do Bloco #%d é inválido. Esperado: %s | Calculado: %s. O conteúdo deste bloco foi adulterado.",
          blocoAtual.getNumeroBloco(),
          blocoAtual.getHashAtual(),
          hashCalculado
        );
        return new BlockchainValidateDTO(false, message);
      }

      // VERIFICAÇÃO 2: Encadeamento do Hash Anterior
      if (!blocoAtual.getHashAnterior().equals(blocoAnterior.getHashAtual())) {
        String message = String.format(
          "QUEBRA DE CADEIA: O Bloco #%d aponta para o hash anterior errado. O Bloco #%d deveria ter o hash %s, mas tem %s. O encadeamento está quebrado.",
          blocoAtual.getNumeroBloco(),
          blocoAtual.getNumeroBloco(),
          blocoAnterior.getHashAtual(),
          blocoAtual.getHashAnterior()
        );
        return new BlockchainValidateDTO(false, message);
      }
    }

    String message = String.format("SUCESSO: A cadeia de blocos está íntegra e válida. Total de blocos: %d.", blocos.size());
    return new BlockchainValidateDTO(true, message);
  }


}
