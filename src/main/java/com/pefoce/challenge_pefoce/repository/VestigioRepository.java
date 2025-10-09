package com.pefoce.challenge_pefoce.repository;

import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
// Interface principal do Spring Data JPA que fornece os métodos CRUD.
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VestigioRepository extends JpaRepository<Vestigio, UUID> {

}