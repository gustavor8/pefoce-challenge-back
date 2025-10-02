package com.pefoce.challenge_pefoce.repository;

import com.pefoce.challenge_pefoce.entity.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferenciaRepository extends JpaRepository<Transferencia, UUID> {
// os métodos de crud o jpa insere
}