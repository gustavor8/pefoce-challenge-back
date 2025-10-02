package com.pefoce.challenge_pefoce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Enumeração dos métodos HTTP (GET, POST, etc).
import org.springframework.http.HttpMethod;
// O gerenciador principal que processa uma requisição de autenticação.
import org.springframework.security.authentication.AuthenticationManager;
// Classe de configuração para obter o AuthenticationManager.
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// Classe principal para configurar a segurança web.
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// Habilita a integração do Spring Security com o Spring MVC.
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// Define as políticas de criação de sessão.
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
// Implementação de PasswordEncoder que usa o algoritmo BCrypt.
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// Interface para codificar senhas.
import org.springframework.security.crypto.password.PasswordEncoder;
// A cadeia de filtros de segurança que será aplicada às requisições HTTP.
import org.springframework.security.web.SecurityFilterChain;
// Filtro padrão do Spring Security para autenticação baseada em formulário (usuário/senha).
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

  @Autowired
  private SecurityFilter securityFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(authorize -> authorize
        // Regras para endpoints de autenticação (são POST)
        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register", "/auth/refresh").permitAll()
        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
        .anyRequest().authenticated()
      )
      .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}