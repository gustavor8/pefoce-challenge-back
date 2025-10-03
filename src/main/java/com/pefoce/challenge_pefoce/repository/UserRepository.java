package com.pefoce.challenge_pefoce.repository;

import com.pefoce.challenge_pefoce.entity.Users;
// Fornece a funcionalidade de um repositório JPA
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
  // O Spring Data JPA cria o crud
  Optional<Users> findByUsername(String username);
}