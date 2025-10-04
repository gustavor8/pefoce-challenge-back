package com.pefoce.challenge_pefoce.service.blockchain;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.entity.BlocoBlockchain;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.util.HashUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockchainValidateTransferencia {
  private final BlockchainService blockchainService;

  public BlockchainValidateTransferencia(BlockchainService blockchainService) {
    this.blockchainService = blockchainService;
  }

  public BlockchainValidateDTO validarHistorico(List<Transferencia> transferencias) {
    for (Transferencia t : transferencias) {
      String hashCalculado = HashUtils.calculateTransferHash(t);
      if (!hashCalculado.equals(t.getHashTransacao())) {
        String mensagem = String.format("ERRO DE INTEGRIDADE: Os dados da Transferência ID %s foram adulterados.", t.getId());
        return new BlockchainValidateDTO(false, mensagem);
      }
      BlocoBlockchain bloco = t.getBlocoBlockchain();
      if (bloco!=null) {
        String hashBlocoCalculado = blockchainService.calcularHashBloco(bloco.getNumeroBloco(), bloco.getHashAnterior(), bloco.getTransacoes());
        if (!hashBlocoCalculado.equals(bloco.getHashAtual())) {
          String mensagem = String.format("ERRO DE INTEGRIDADE: O Bloco #%d, que armazena a Transferência ID %s, foi adulterado.", bloco.getNumeroBloco(), t.getId());
          return new BlockchainValidateDTO(false, mensagem);
        }
      }
    }
    return new BlockchainValidateDTO(true, "A cadeia de custódia para este vestígio está íntegra.");
  }
}