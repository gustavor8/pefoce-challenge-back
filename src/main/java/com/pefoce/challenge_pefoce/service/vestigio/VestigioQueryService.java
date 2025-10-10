package com.pefoce.challenge_pefoce.service.vestigio;

import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioStatusResponseDTO;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

  @Cacheable(value = "vestigios", key = "#id")
  @Transactional(readOnly = true)
  public VestigioDTO buscarPorId(UUID id) {
    return vestigioRepository.findById(id)
      .map(vestigioMapper::toDTO)
      .orElseThrow(() -> new EntityNotFoundException("Vestígio não encontrado com o ID: " + id));
  }

  @Transactional(readOnly = true)
  public VestigioStatusResponseDTO buscarStatusPorId(UUID id) {
    Vestigio vestigio = vestigioRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Vestígio não encontrado com o ID: " + id));
    return new VestigioStatusResponseDTO(vestigio.getId(), vestigio.getStatus().name());
  }
}