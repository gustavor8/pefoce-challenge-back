package com.pefoce.challenge_pefoce.util;

import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import java.security.MessageDigest; // Algoritmos de hash.
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HashUtilsTest {
 // Grande parte dos testes já haviam sido feitas em outras classes
  @Test
  @DisplayName("Deve calcular o hash de uma transferência de forma consistente")
  void calculateTransferHash_deveGerarHashCorretoEEstavel() {
    Usuario origem = mock(Usuario.class);
    when(origem.getId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    Usuario destino = mock(Usuario.class);
    when(destino.getId()).thenReturn(UUID.fromString("22222222-2222-2222-2222-222222222222"));
    Vestigio v1 = mock(Vestigio.class);
    when(v1.getId()).thenReturn(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
    Vestigio v2 = mock(Vestigio.class);
    when(v2.getId()).thenReturn(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
    Transferencia transferencia = mock(Transferencia.class);
    when(transferencia.getResponsavelOrigem()).thenReturn(origem);
    when(transferencia.getResponsavelDestino()).thenReturn(destino);
    when(transferencia.getMotivo()).thenReturn("Teste de Hash");
    when(transferencia.getVestigios()).thenReturn(Set.of(v2, v1));
    String dadosConcatenadosManualmente = "11111111-1111-1111-1111-111111111111" +
      "22222222-2222-2222-2222-222222222222" +
      "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" +
      "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb" +
      "Teste de Hash";
    String hashEsperado = HashUtils.applySha256(dadosConcatenadosManualmente);
    String hashCalculado = HashUtils.calculateTransferHash(transferencia);
    assertEquals(hashEsperado, hashCalculado);
  }

  @Test
  @DisplayName("Deve lançar RuntimeException quando o algoritmo SHA-256 não é encontrado")
  void applySha256_quandoAlgoritmoNaoExiste_deveLancarRuntimeException() {
    try (MockedStatic<MessageDigest> mockedDigest = mockStatic(MessageDigest.class)) {
      mockedDigest.when(() -> MessageDigest.getInstance("SHA-256"))
        .thenThrow(new NoSuchAlgorithmException("Algoritmo de teste não encontrado"));
      RuntimeException exception = assertThrows(RuntimeException.class,
        () -> HashUtils.applySha256("qualquer-entrada")
      );
      assertEquals("Erro ao gerar o hash SHA-256.", exception.getMessage());
      assertInstanceOf(NoSuchAlgorithmException.class, exception.getCause());
    }
  }
}