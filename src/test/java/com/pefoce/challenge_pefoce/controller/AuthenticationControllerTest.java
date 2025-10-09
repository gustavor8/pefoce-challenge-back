package com.pefoce.challenge_pefoce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pefoce.challenge_pefoce.dto.login.LoginRequestDTO;
import com.pefoce.challenge_pefoce.dto.usuario.GetUsuarioDTO;
import com.pefoce.challenge_pefoce.dto.usuario.UsuarioRegisterDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.service.auth.TokenService;
import com.pefoce.challenge_pefoce.service.usuario.UsuarioRegisterService;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthenticationController.class,
  excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AuthenticationControllerTest {
  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  @MockBean
  private AuthenticationManager authenticationManager;
  @MockBean
  private TokenService tokenService;
  @MockBean
  private UsuarioRegisterService usuarioRegisterService;
  @MockBean
  private UsuarioRepository usuarioRepository;
  @Autowired
  AuthenticationControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  @Test
  @DisplayName("Login - Deve retornar 403 Forbidden com credenciais inválidas")
  void login_comCredenciaisInvalidas_deveRetornar403Forbidden() throws Exception {
    var loginRequest = new LoginRequestDTO("user", "wrong_password");
    when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Credenciais inválidas"));
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Register - Deve retornar 201 Created com dados de registro válidos")
  void register_comDadosValidos_deveRetornar201Created() throws Exception {
    var registerRequest = new UsuarioRegisterDTO("newUser", "password", "Novo Usuário", "new@user.com", "Cargo", "Depto", null);
    var usuarioCriado = new GetUsuarioDTO(UUID.randomUUID(), "newUser", "Novo Usuário", "new@user.com", "Cargo", "Depto", true, OffsetDateTime.now(), OffsetDateTime.now());
    when(usuarioRegisterService.registerUser(registerRequest)).thenReturn(usuarioCriado);
    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.message").value("Usuário 'newUser' foi criado com sucesso!"))
      .andExpect(jsonPath("$.name").value("Novo Usuário"));
  }

  @Test
  @DisplayName("Register - Deve retornar 409 Conflict se o usuário já existir")
  void register_comUsuarioDuplicado_deveRetornar409Conflict() throws Exception {
    var registerRequest = new UsuarioRegisterDTO("existingUser", "password", "Nome", "email@email.com", "Cargo", "Depto", null);
    when(usuarioRegisterService.registerUser(registerRequest)).thenThrow(new DataIntegrityViolationException("Usuário já existe"));
    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
      .andExpect(status().isConflict());
  }
}