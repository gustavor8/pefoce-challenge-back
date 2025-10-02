package com.pefoce.challenge_pefoce.service.transferencia;

import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.repository.TransferenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TransferenciaQueryService {

  private final TransferenciaRepository transferenciaRepository;
  private final TransferenciaMapper transferenciaMapper;

  public TransferenciaQueryService(TransferenciaRepository transferenciaRepository, TransferenciaMapper transferenciaMapper) {
    this.transferenciaRepository = transferenciaRepository;
    this.transferenciaMapper = transferenciaMapper;
  }

  @Transactional(readOnly = true)
  public List<TransferenciaDTO> listarTodas() {
    return transferenciaRepository.findAll().stream()
      .map(transferenciaMapper::toDTO)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public TransferenciaDTO buscarPorId(UUID id) {
    return transferenciaRepository.findById(id)
      .map(transferenciaMapper::toDTO)
      .orElseThrow(() -> new EntityNotFoundException("Transferência não encontrada com o ID: " + id));
  }
}