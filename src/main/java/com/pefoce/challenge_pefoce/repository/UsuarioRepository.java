package com.pefoce.challenge_pefoce.repository;

import com.pefoce.challenge_pefoce.entity.Usuario;
// Fornece a funcionalidade de um repositório JPA
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

  Optional<Usuario> findByUsername(String username);
}