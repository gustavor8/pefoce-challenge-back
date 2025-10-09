package com.pefoce.challenge_pefoce.controller;

import com.pefoce.challenge_pefoce.dto.shared.ErrorResponseDTO;
import com.pefoce.challenge_pefoce.dto.login.LoginRequestDTO;
import com.pefoce.challenge_pefoce.dto.login.LoginResponseDTO;
import com.pefoce.challenge_pefoce.dto.usuario.GetUsuarioDTO;
import com.pefoce.challenge_pefoce.dto.usuario.UsuarioRegisterDTO;
import com.pefoce.challenge_pefoce.dto.usuario.UsuarioRegisterResponseDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.service.auth.TokenService;
import com.pefoce.challenge_pefoce.service.usuario.UsuarioRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders; // Constantes para nomes de cabeçalhos HTTP (ex: "Set-Cookie").
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie; // Classe para construir um cookie de resposta de forma moderna.
import org.springframework.http.ResponseEntity; // Classe para construir respostas HTTP completas (status, cabeçalhos, corpo).
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Exceção padrão para usuário não encontrado.
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para login, registro e renovação de token.")
public class AuthenticationController {

  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private TokenService tokenService;
  @Autowired
  private UsuarioRegisterService usuarioRegisterService;
  @Autowired
  private UsuarioRepository usuarioRepository;

  @Operation(summary = "Realiza o login de um usuário")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
    @ApiResponse(responseCode = "403", description = "Acesso negado - credenciais inválidas", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequest) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
    Authentication auth = this.authenticationManager.authenticate(usernamePassword);
    Usuario userAuthenticated = (Usuario) auth.getPrincipal();

    String accessToken = tokenService.generateAccessToken(userAuthenticated);
    String refreshToken = tokenService.generateRefreshToken(userAuthenticated);

    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
      .httpOnly(true)
      .secure(true) // Em produção, deve ser true para exigir HTTPS
      .path("/")
      .maxAge(30 * 60) // 30 minutos
      .build();

    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, cookie.toString())
      .body(new LoginResponseDTO(accessToken, userAuthenticated.getNome()));
  }

  @Operation(summary = "Cadastrar usuário")
  @PostMapping("/register")
  public ResponseEntity<UsuarioRegisterResponseDTO> register(@RequestBody @Valid UsuarioRegisterDTO usuarioRegisterDTO) {
    GetUsuarioDTO usuarioCriado = usuarioRegisterService.registerUser(usuarioRegisterDTO);
    String mensagem = "Usuário '" + usuarioCriado.username() + "' foi criado com sucesso!";

    UsuarioRegisterResponseDTO resposta = new UsuarioRegisterResponseDTO(
      mensagem,
      usuarioCriado.nome()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
  }

  @Operation(summary = "Renova o access token usando o refresh token")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Access token renovado com sucesso", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
    @ApiResponse(responseCode = "403", description = "Refresh token inválido ou expirado", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PostMapping("/refresh")
  public ResponseEntity<LoginResponseDTO> refreshToken(@Parameter(hidden = true) @CookieValue(name = "refreshToken") String refreshToken) {
    String username = tokenService.validateToken(refreshToken);
    Usuario user = usuarioRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("Usuário associado ao token de refresh não encontrado"));
    String newAccessToken = tokenService.generateAccessToken(user);

    return ResponseEntity.ok(new LoginResponseDTO(newAccessToken, username));
  }
}