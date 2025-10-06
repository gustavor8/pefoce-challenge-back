package com.pefoce.challenge_pefoce.service.blockchain;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.entity.Blockchain;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.repository.BlockchainRepository;
import com.pefoce.challenge_pefoce.util.HashUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlockchainService {
  private final BlockchainRepository blocoBlockchainRepository;

  public BlockchainService(BlockchainRepository blocoBlockchainRepository) {
    this.blocoBlockchainRepository = blocoBlockchainRepository;
  }

  public Blockchain criarNovoBloco(Set<Transferencia> transacoes) {
    Optional<Blockchain> ultimoBloco = blocoBlockchainRepository.findFirstByOrderByNumeroBlocoDesc();
    Long numeroBloco = ultimoBloco.map(b -> b.getNumeroBloco() + 1).orElse(1L);
    String hashAnterior = ultimoBloco.map(Blockchain::getHashAtual).orElse("0");

    String hashAtual = calcularHashBloco(numeroBloco, hashAnterior, transacoes);

    Blockchain novoBloco = Blockchain.builder()
      .numeroBloco(numeroBloco)
      .hashAnterior(hashAnterior)
      .hashAtual(hashAtual)
      .carimboDeTempo(LocalDateTime.now())
      .build();

    for (Transferencia t : transacoes) {
      t.setBlockchain(novoBloco);
    }
    novoBloco.setTransacoes(transacoes);

    return blocoBlockchainRepository.save(novoBloco);
  }

  public String calcularHashBloco(Long numeroBloco, String hashAnterior, Set<Transferencia> transacoes) {
    String hashesTransacoesConcatenados = transacoes.stream()
      .map(Transferencia::getHashTransacao)
      .sorted() // Ordena os hashes alfabeticamente
      .collect(Collectors.joining()); // Concatena em uma única String

    String dadosDoBloco = numeroBloco.toString() + hashAnterior + hashesTransacoesConcatenados;

    return HashUtils.applySha256(dadosDoBloco);
  }


  public BlockchainValidateDTO validarBlockchain() {
    List<Blockchain> blocos = blocoBlockchainRepository.findAllByOrderByNumeroBlocoAsc();

    if (blocos.isEmpty()) {
      return new BlockchainValidateDTO(true, "A cadeia de blocos é válida (está vazia).");
    }

    Blockchain blocoGenesis = blocos.get(0);
    if (!"0".equals(blocoGenesis.getHashAnterior())) {
      return new BlockchainValidateDTO(false, "ERRO DE INTEGRIDADE: O hash anterior do Bloco Gênese #1 não é '0'.");
    }
    String hashGenesisCalculado = calcularHashBloco(blocoGenesis.getNumeroBloco(), blocoGenesis.getHashAnterior(), blocoGenesis.getTransacoes());
    if (!hashGenesisCalculado.equals(blocoGenesis.getHashAtual())) {
      return new BlockchainValidateDTO(false, String.format(
        "ERRO DE INTEGRIDADE: O hash do Bloco Gênese #1 é inválido. O conteúdo deste bloco foi adulterado. Esperado: %s | Calculado: %s",
        blocoGenesis.getHashAtual(),
        hashGenesisCalculado));
    }

    for (int i = 1; i < blocos.size(); i++) {
      Blockchain blocoAtual = blocos.get(i);
      Blockchain blocoAnterior = blocos.get(i - 1);

      if (!blocoAtual.getHashAnterior().equals(blocoAnterior.getHashAtual())) {
        return new BlockchainValidateDTO(false, String.format(
          "QUEBRA DE CADEIA: O hash anterior do Bloco #%d está incorreto. A cadeia foi quebrada entre os blocos #%d e #%d.",
          blocoAtual.getNumeroBloco(),
          blocoAnterior.getNumeroBloco(),
          blocoAtual.getNumeroBloco()
        ));
      }

      String hashCalculado = calcularHashBloco(blocoAtual.getNumeroBloco(), blocoAtual.getHashAnterior(), blocoAtual.getTransacoes());
      if (!hashCalculado.equals(blocoAtual.getHashAtual())) {
        return new BlockchainValidateDTO(false, String.format(
          "ERRO DE INTEGRIDADE: O hash do Bloco #%d é inválido. O conteúdo deste bloco foi adulterado.",
          blocoAtual.getNumeroBloco()
        ));
      }
    }

    String message = String.format("SUCESSO: A cadeia de blocos está íntegra e válida. Total de blocos: %d.", blocos.size());
    return new BlockchainValidateDTO(true, message);
  }
}