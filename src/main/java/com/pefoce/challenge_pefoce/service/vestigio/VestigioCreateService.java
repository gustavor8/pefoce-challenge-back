package com.pefoce.challenge_pefoce.service.vestigio;


import com.pefoce.challenge_pefoce.dto.vestigio.VestigioCreateDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import com.pefoce.challenge_pefoce.entity.vestigio.StatusVestigio;

import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

@Service
public class VestigioCreateService {

  private final VestigioRepository vestigioRepository;
  private final UsuarioRepository usuarioRepository;
  private final VestigioMapper vestigioMapper;

  public VestigioCreateService(VestigioRepository vestigioRepository, UsuarioRepository usuarioRepository, VestigioMapper vestigioMapper) {
    this.vestigioRepository = vestigioRepository;
    this.usuarioRepository = usuarioRepository;
    this.vestigioMapper = vestigioMapper;
  }


  @Transactional
  public VestigioDTO criarVestigio(VestigioCreateDTO createDTO) {
    Usuario responsavel = usuarioRepository.findById(createDTO.responsavelAtualId())
      .orElseThrow(() -> new EntityNotFoundException("Usuário responsável não encontrado."));
    Vestigio novoVestigio = Vestigio.builder()
      .tipo(createDTO.tipo())
      .descricao(createDTO.descricao())
      .localColeta(createDTO.localColeta())
      .dataColeta(createDTO.dataColeta())
      .responsavelAtual(responsavel)
      .status(StatusVestigio.COLETADO)
      .build();

    Vestigio vestigioSalvo = vestigioRepository.save(novoVestigio);

    return vestigioMapper.toDTO(vestigioSalvo);
  }
}