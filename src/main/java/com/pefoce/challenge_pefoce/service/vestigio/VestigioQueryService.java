package com.pefoce.challenge_pefoce.service.vestigio;

import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; // Classe do Java com métodos para coletar elementos de uma Stream.

@Service
public class VestigioQueryService {

  private final VestigioRepository vestigioRepository;
  private final VestigioMapper vestigioMapper;

  public VestigioQueryService(VestigioRepository vestigioRepository, VestigioMapper vestigioMapper) {
    this.vestigioRepository = vestigioRepository;
    this.vestigioMapper = vestigioMapper;
  }

  @Transactional(readOnly = true)
  public List<VestigioDTO> listarTodos() {
    return vestigioRepository.findAll()
      .stream()
      .map(vestigioMapper::toDTO)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public VestigioDTO buscarPorId(UUID id) {
    return vestigioRepository.findById(id)
      // 2. Se o Optional contiver um vestígio converte para DTO
      .map(vestigioMapper::toDTO)
      // 3. Se o Optional estiver vazio, lança uma exceção.
      .orElseThrow(() -> new EntityNotFoundException("Vestígio não encontrado com o ID: " + id));
  }
}