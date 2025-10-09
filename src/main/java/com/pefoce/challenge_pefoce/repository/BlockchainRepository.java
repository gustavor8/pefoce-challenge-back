package com.pefoce.challenge_pefoce.repository;

import com.pefoce.challenge_pefoce.entity.Blockchain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlockchainRepository extends JpaRepository<Blockchain, UUID> {

  // Busca o ultimo registro, o nome é pra passar os dados pro spring
  Optional<Blockchain> findFirstByOrderByNumeroBlocoDesc();

  // Busca todos os blocos em ordem crescente
  List<Blockchain> findAllByOrderByNumeroBlocoAsc();
}
