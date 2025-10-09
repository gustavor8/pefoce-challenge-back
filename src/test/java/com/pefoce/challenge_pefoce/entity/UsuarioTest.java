package com.pefoce.challenge_pefoce.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

// Obs: Os teste dos métodos getPassword e getUserName já foram testados no authentication service e no DataSeeder
class UsuarioTest {
  private Usuario usuario;

  @BeforeEach
  void setUp() {
    usuario = new Usuario();
  }

  @Test
  @DisplayName("Deve retornar a autoridade 'ROLE_USER' corretamente")
  void getAuthorities_deveRetornarRoleUser() {
    Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();
    assertNotNull(authorities);
    assertEquals(1, authorities.size());
    GrantedAuthority authority = authorities.iterator().next();
    assertEquals("ROLE_USER", authority.getAuthority());
    assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
  }
  @Test
  @DisplayName("isEnabled deve retornar o valor do campo 'ativo'")
  void isEnabled_deveRefletirOStatusDoCampoAtivo() {
    usuario.setAtivo(true);
    assertTrue(usuario.isEnabled(), "Deveria retornar true quando o usuário está ativo.");
    usuario.setAtivo(false);
    assertFalse(usuario.isEnabled(), "Deveria retornar false quando o usuário está inativo.");
  }

  @Test
  @DisplayName("Deve retornar 'true' para status de conta não expirada, não bloqueada e credenciais não expiradas")
  void metodosDeStatusDaConta_devemSempreRetornarTrue() {
    assertTrue(usuario.isAccountNonExpired(), "A conta nunca deve expirar.");
    assertTrue(usuario.isAccountNonLocked(), "A conta nunca deve ser bloqueada.");
    assertTrue(usuario.isCredentialsNonExpired(), "As credenciais nunca devem expirar.");
  }
}