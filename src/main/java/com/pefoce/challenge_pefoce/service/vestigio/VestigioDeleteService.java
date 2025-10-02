package com.pefoce.challenge_pefoce.service.vestigio;

import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class VestigioDeleteService {
  private final VestigioRepository vestigioRepository;

  public VestigioDeleteService(VestigioRepository vestigioRepository) {
    this.vestigioRepository = vestigioRepository;
  }

  @Transactional
  public void deletarVestigio(UUID id) {
    if (!vestigioRepository.existsById(id)) {
      throw new EntityNotFoundException("Vestígio não encontrado com o ID: " + id);
    }

    vestigioRepository.deleteById(id);
  }
}