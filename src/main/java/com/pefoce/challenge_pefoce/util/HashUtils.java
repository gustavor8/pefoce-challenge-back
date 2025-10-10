package com.pefoce.challenge_pefoce.util;

import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;

import java.nio.charset.StandardCharsets; // Converte  em UTF-8
import java.security.MessageDigest; // implementar algoritmos de hash.
import java.security.NoSuchAlgorithmException; // exceção do algoritmo de hash
import java.util.Comparator; // Para ordenação dos Vestígios

public class HashUtils {

  public static String applySha256(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

      StringBuilder hexString = new StringBuilder();
      // Converte os bytes em hexadecimal
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length()==1) {
          hexString.append('0'); // mantem sempre 2 algarismos no hexadecimal
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Erro ao gerar o hash SHA-256.", e);
    }
  }

  public static String calculateTransferHash(Transferencia transferencia) {
    String vestigioIdsConcatenados = transferencia.getVestigios().stream()
      // Mapeia os id do vestigio
      .map(Vestigio::getId)
      .map(Object::toString)
      // Coloca em ordem para não quebrar o hash
      .sorted(Comparator.naturalOrder())
      .reduce("", String::concat);

    String dadosDaTransacao =
      transferencia.getResponsavelOrigem().getId().toString() +
        transferencia.getResponsavelDestino().getId().toString() +
        vestigioIdsConcatenados +
        (transferencia.getMotivo()!=null ? transferencia.getMotivo():"");

    return applySha256(dadosDaTransacao);
  }
}
