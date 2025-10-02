package com.pefoce.challenge_pefoce.service.transferencia;

import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaCreateDTO;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.entity.Users;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import com.pefoce.challenge_pefoce.entity.vestigio.StatusVestigio;
import com.pefoce.challenge_pefoce.repository.TransferenciaRepository;
import com.pefoce.challenge_pefoce.repository.UserRepository;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class TransferenciaCreateService {

  private final TransferenciaRepository transferenciaRepository;
  private final UserRepository userRepository;
  private final VestigioRepository vestigioRepository;
  private final TransferenciaMapper transferenciaMapper;

  public TransferenciaCreateService(TransferenciaRepository transferenciaRepository,
                                    UserRepository userRepository,
                                    VestigioRepository vestigioRepository,
                                    TransferenciaMapper transferenciaMapper) {
    this.transferenciaRepository = transferenciaRepository;
    this.userRepository = userRepository;
    this.vestigioRepository = vestigioRepository;
    this.transferenciaMapper = transferenciaMapper;
  }


  @Transactional
  public TransferenciaDTO criar(TransferenciaCreateDTO dto, Users responsavelOrigem) {
    Users responsavelDestino = userRepository.findById(dto.responsavelDestinoId())
      .orElseThrow(() -> new EntityNotFoundException("Responsável de destino não encontrado."));

    Set<Vestigio> vestigios = new HashSet<>(vestigioRepository.findAllById(dto.vestigioIds()));
    if (vestigios.size()!=dto.vestigioIds().size()) {
      throw new EntityNotFoundException("Um ou mais vestígios não foram encontrados.");
    }

    for (Vestigio v : vestigios) {
      if (!v.getResponsavelAtual().getId().equals(responsavelOrigem.getId())) {
        throw new SecurityException("O usuário não tem a custódia do vestígio de ID: " + v.getId());
      }
    }

    Transferencia novaTransferencia = Transferencia.builder()
      .responsavelOrigem(responsavelOrigem)
      .responsavelDestino(responsavelDestino)
      .motivo(dto.motivo())
      .vestigios(vestigios)
      .build();

    Transferencia transferenciaSalva = transferenciaRepository.save(novaTransferencia);

    vestigios.forEach(v -> {
      v.setResponsavelAtual(responsavelDestino);
      v.setStatus(StatusVestigio.EM_ANALISE);
    });
    vestigioRepository.saveAll(vestigios);

    return transferenciaMapper.toDTO(transferenciaSalva);
  }
}