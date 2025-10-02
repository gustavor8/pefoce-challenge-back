package com.pefoce.challenge_pefoce.service;

import com.pefoce.challenge_pefoce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
// Interface que define um usuário para o Spring Security.
import org.springframework.security.core.userdetails.UserDetails;
// Interface que define o contrato para um serviço que carrega dados de usuários.
import org.springframework.security.core.userdetails.UserDetailsService;
// Exceção lançada quando um usuário não é encontrado.
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// Marca esta classe como um "Serviço" do Spring, tornando-a um bean gerenciado.
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o nome: " + username));
  }
}