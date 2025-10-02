package com.pefoce.challenge_pefoce.repository;

import com.pefoce.challenge_pefoce.entity.Users;
// Fornece a funcionalidade de um repositório JPA (operações de CRUD, etc).
import org.springframework.data.jpa.repository.JpaRepository;
// Interface que define os dados de um usuário (usada como tipo de retorno).
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
  // O Spring Data JPA cria a implementação deste método automaticamente com base no nome.
  Optional<Users> findByUsername(String username);
}