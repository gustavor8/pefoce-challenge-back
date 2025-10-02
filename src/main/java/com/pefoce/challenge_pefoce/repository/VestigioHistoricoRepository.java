package com.pefoce.challenge_pefoce.repository;

import com.pefoce.challenge_pefoce.entity.VestigioHistorico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VestigioHistoricoRepository extends JpaRepository<VestigioHistorico, UUID> {
// os métodos de crud o jpa insere
}