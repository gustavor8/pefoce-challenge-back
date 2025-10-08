package com.pefoce.challenge_pefoce.service.transferencia;

import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
class TransferenciaMapperTest {
  private TransferenciaMapper transferenciaMapper;
  @BeforeEach
  void setUp() {
    transferenciaMapper = new TransferenciaMapper();
  }

  @Test
  @DisplayName("Deve mapear corretamente uma entidade Transferencia completa para um TransferenciaDTO")
  void toDTO_comTransferenciaCompleta_deveMapearTodosOsCampos() {
    Usuario origem = new Usuario();
    origem.setId(UUID.randomUUID());
    origem.setNome("Usuario Origem");
    Usuario destino = new Usuario();
    destino.setId(UUID.randomUUID());
    destino.setNome("Usuario Destino");
    Vestigio vestigio = new Vestigio();
    vestigio.setId(UUID.randomUUID());
    vestigio.setTipo("Smartphone");
    Transferencia transferencia = new Transferencia();
    transferencia.setId(UUID.randomUUID());
    transferencia.setMotivo("Análise Pericial");
    transferencia.setDataTransferencia(OffsetDateTime.now());
    transferencia.setHashTransacao("hash123abc");
    transferencia.setResponsavelOrigem(origem);
    transferencia.setResponsavelDestino(destino);
    transferencia.setVestigios(Set.of(vestigio));
    TransferenciaDTO dto = transferenciaMapper.toDTO(transferencia);
    assertNotNull(dto);
    assertEquals(transferencia.getId(), dto.id());
    assertEquals(transferencia.getMotivo(), dto.motivo());
    assertEquals(transferencia.getDataTransferencia(), dto.dataTransferencia());
    assertEquals(transferencia.getHashTransacao(), dto.hashTransacao());

    assertNotNull(dto.responsavelOrigem());
    assertEquals(origem.getId(), dto.responsavelOrigem().id());
    assertEquals(origem.getNome(), dto.responsavelOrigem().nome());
    assertNotNull(dto.responsavelDestino());
    assertEquals(destino.getId(), dto.responsavelDestino().id());
    assertEquals(destino.getNome(), dto.responsavelDestino().nome());
    assertNotNull(dto.vestigios());
    assertEquals(1, dto.vestigios().size());
    var vestigioDTO = dto.vestigios().iterator().next();
    assertEquals(vestigio.getId(), vestigioDTO.id());
    assertEquals(vestigio.getTipo(), vestigioDTO.tipo());
  }

  @Test
  @DisplayName("Deve mapear responsáveis como nulos se eles forem nulos na entidade")
  void toDTO_comUsuariosNulos_deveMapearCamposComoNulos() {
    Transferencia transferencia = new Transferencia();
    transferencia.setId(UUID.randomUUID());
    transferencia.setResponsavelOrigem(null);
    transferencia.setResponsavelDestino(null);
    transferencia.setVestigios(Set.of()); // Define um conjunto vazio para evitar NullPointerException.
    TransferenciaDTO dto = transferenciaMapper.toDTO(transferencia);
    assertNotNull(dto);
    assertNull(dto.responsavelOrigem());
    assertNull(dto.responsavelDestino());
  }

  @Test
  @DisplayName("Deve ignorar vestígios nulos dentro do conjunto de vestígios")
  void toDTO_comVestigioNuloNoSet_deveIgnorarVestigioNulo() {
    Vestigio vestigioValido = new Vestigio();
    vestigioValido.setId(UUID.randomUUID());
    vestigioValido.setTipo("Notebook");
    Set<Vestigio> vestigiosComNulo = new HashSet<>();
    vestigiosComNulo.add(vestigioValido);
    vestigiosComNulo.add(null); // Adiciona um valor nulo ao conjunto.
    Transferencia transferencia = new Transferencia();
    transferencia.setId(UUID.randomUUID());
    transferencia.setVestigios(vestigiosComNulo);
    TransferenciaDTO dto = transferenciaMapper.toDTO(transferencia);
    assertNotNull(dto);
    assertNotNull(dto.vestigios());
    assertEquals(1, dto.vestigios().size());
    assertEquals(vestigioValido.getId(), dto.vestigios().iterator().next().id());
  }
}