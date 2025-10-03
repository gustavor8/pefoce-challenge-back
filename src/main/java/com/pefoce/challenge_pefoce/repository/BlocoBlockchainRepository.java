package com.pefoce.challenge_pefoce.repository;

import com.pefoce.challenge_pefoce.entity.BlocoBlockchain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlocoBlockchainRepository extends JpaRepository<BlocoBlockchain, UUID> {
  // spring implementa o crud
  // Busca o ultimo registro, o nome é pra passar os dados pro spring
  Optional<BlocoBlockchain> findFirstByOrderByNumeroBlocoDesc();

  // Busca todos os blocos em ordem crescente
  List<BlocoBlockchain> findAllByOrderByNumeroBlocoAsc();
}
