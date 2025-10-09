package com.pefoce.challenge_pefoce.service.vestigio;

import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO; import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.entity.vestigio.StatusVestigio;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class VestigioMapperTest {
  private VestigioMapper vestigioMapper;

  @BeforeEach
  void setUp() {
    vestigioMapper = new VestigioMapper();
  }

  @Test
  @DisplayName("Deve mapear corretamente uma entidade Vestigio completa para um VestigioDTO")
  void toDTO_comVestigioCompleto_deveMapearTodosOsCampos() {
    Usuario responsavel = new Usuario();
    responsavel.setId(UUID.randomUUID());
    responsavel.setNome("Perito João Silva");
    Vestigio vestigio = new Vestigio();
    vestigio.setId(UUID.randomUUID());
    vestigio.setTipo("Arma de Fogo");
    vestigio.setDescricao("Pistola calibre .40");
    vestigio.setLocalColeta("Rua das Flores, 123");
    vestigio.setDataColeta(OffsetDateTime.now().minusDays(5));
    vestigio.setStatus(StatusVestigio.COLETADO);
    vestigio.setCriadoEm(OffsetDateTime.now().minusDays(5));
    vestigio.setAtualizadoEm(OffsetDateTime.now());
    vestigio.setResponsavelAtual(responsavel);
    VestigioDTO dto = vestigioMapper.toDTO(vestigio);
    assertNotNull(dto, "O DTO resultante não deveria ser nulo.");
    assertEquals(vestigio.getId(), dto.id());
    assertEquals(vestigio.getTipo(), dto.tipo());
    assertEquals(vestigio.getDescricao(), dto.descricao());
    assertEquals(vestigio.getLocalColeta(), dto.localColeta());
    assertEquals(vestigio.getDataColeta(), dto.dataColeta());
    assertEquals(vestigio.getStatus().name(), dto.status()); // Verifica se o Enum foi convertido para String.
    assertEquals(vestigio.getCriadoEm(), dto.criadoEm());
    assertEquals(vestigio.getAtualizadoEm(), dto.atualizadoEm());
    assertNotNull(dto.responsavelAtual(), "O DTO do responsável não deveria ser nulo.");
    assertEquals(responsavel.getId(), dto.responsavelAtual().id());
    assertEquals(responsavel.getNome(), dto.responsavelAtual().nome());
  }

  @Test
  @DisplayName("Deve mapear o responsável como nulo se ele for nulo na entidade Vestigio")
  void toDTO_comResponsavelNulo_deveMapearResponsavelComoNulo() {
    Vestigio vestigio = new Vestigio();
    vestigio.setId(UUID.randomUUID());
    vestigio.setStatus(StatusVestigio.DESCARTADO); // Necessário para evitar NullPointerException no .name()
    vestigio.setResponsavelAtual(null);
    VestigioDTO dto = vestigioMapper.toDTO(vestigio);
    assertNotNull(dto);
    assertNull(dto.responsavelAtual(), "O DTO do responsável deveria ser nulo.");
  }
}