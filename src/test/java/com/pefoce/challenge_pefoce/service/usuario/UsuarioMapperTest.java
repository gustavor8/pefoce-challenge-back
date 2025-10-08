package com.pefoce.challenge_pefoce.service.usuario;

import com.pefoce.challenge_pefoce.dto.usuario.GetUsuarioDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UsuarioMapperTest {
  private UsuarioMapper usuarioMapper;
  @BeforeEach
  void setUp() {
    usuarioMapper = new UsuarioMapper();
  }

  @Test
  @DisplayName("Deve mapear corretamente uma entidade Usuario para um GetUsuarioDTO")
  void toDTO_deveMapearTodosOsCamposCorretamente() {
    Usuario usuarioEntidade = new Usuario();
    usuarioEntidade.setId(UUID.randomUUID());
    usuarioEntidade.setUsername("joao.costa");
    usuarioEntidade.setNome("João Costa");
    usuarioEntidade.setEmail("joao.costa@pefoce.ce.gov.br");
    usuarioEntidade.setCargo("Perito Criminal");
    usuarioEntidade.setDepartamento("NUCRIM");
    usuarioEntidade.setAtivo(true);
    usuarioEntidade.setCriadoEm(OffsetDateTime.now().minusDays(10));
    usuarioEntidade.setAtualizadoEm(OffsetDateTime.now());
    GetUsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuarioEntidade);
    assertNotNull(usuarioDTO, "O DTO resultante não deveria ser nulo.");
    assertEquals(usuarioEntidade.getId(), usuarioDTO.id());
    assertEquals(usuarioEntidade.getUsername(), usuarioDTO.username());
    assertEquals(usuarioEntidade.getNome(), usuarioDTO.nome());
    assertEquals(usuarioEntidade.getEmail(), usuarioDTO.email());
    assertEquals(usuarioEntidade.getCargo(), usuarioDTO.cargo());
    assertEquals(usuarioEntidade.getDepartamento(), usuarioDTO.departamento());
    assertEquals(usuarioEntidade.isAtivo(), usuarioDTO.ativo());
    assertEquals(usuarioEntidade.getCriadoEm(), usuarioDTO.criadoEm());
    assertEquals(usuarioEntidade.getAtualizadoEm(), usuarioDTO.atualizadoEm());
  }
}
