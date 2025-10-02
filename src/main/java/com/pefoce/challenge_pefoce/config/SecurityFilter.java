package com.pefoce.challenge_pefoce.config;

import com.pefoce.challenge_pefoce.repository.UserRepository;
import com.pefoce.challenge_pefoce.service.TokenService;
// Define a cadeia de filtros por onde uma requisição passa.
import jakarta.servlet.FilterChain;
// Exceção que pode ocorrer durante o processamento de um filtro.
import jakarta.servlet.ServletException;
// Representa uma requisição HTTP.
import jakarta.servlet.http.HttpServletRequest;
// Representa uma resposta HTTP.
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
// Objeto que representa o usuário autenticado, suas permissões e credenciais.
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// Classe central que armazena os detalhes de segurança da requisição atual.
import org.springframework.security.core.context.SecurityContextHolder;
// Interface que define os dados de um usuário.
import org.springframework.security.core.userdetails.UserDetails;
// Marca a classe como um componente gerenciado pelo Spring.
import org.springframework.stereotype.Component;
// Classe base do Spring para garantir que um filtro seja executado apenas uma vez por requisição.
import org.springframework.web.filter.OncePerRequestFilter;

// Exceção relacionada a operações de entrada/saída.
import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  @Autowired
  private TokenService tokenService;
  @Autowired
  private UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
    String token = recoverToken(request);

    if (token!=null) {
      String username = tokenService.validateToken(token);
      var userOptional = userRepository.findByUsername(username);
      if (userOptional.isPresent()) {

        UserDetails user = userOptional.get();

        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader==null || !authHeader.startsWith("Bearer ")) {
      return null;
    }
    return authHeader.substring(7);
  }
}