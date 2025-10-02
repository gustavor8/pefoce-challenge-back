package com.pefoce.challenge_pefoce.service.vestigio;

import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioUpdateDTO;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class VestigioUpdateService {
  private final VestigioRepository vestigioRepository;
  private final VestigioMapper vestigioMapper;

  public VestigioUpdateService(VestigioRepository vestigioRepository, VestigioMapper vestigioMapper) {
    this.vestigioRepository = vestigioRepository;
    this.vestigioMapper = vestigioMapper;
  }

  @Transactional
  public VestigioDTO atualizarVestigio(UUID id, VestigioUpdateDTO updateDTO) {

    Vestigio vestigioExistente = vestigioRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Vestígio não encontrado com o ID: " + id));

    vestigioExistente.setTipo(updateDTO.tipo());
    vestigioExistente.setDescricao(updateDTO.descricao());
    vestigioExistente.setLocalColeta(updateDTO.localColeta());

    Vestigio vestigioAtualizado = vestigioRepository.save(vestigioExistente);

    return vestigioMapper.toDTO(vestigioAtualizado);
  }
}