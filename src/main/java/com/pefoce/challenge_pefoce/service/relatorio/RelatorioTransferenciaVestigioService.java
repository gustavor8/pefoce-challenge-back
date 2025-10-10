package com.pefoce.challenge_pefoce.service.relatorio;

import com.pefoce.challenge_pefoce.dto.blockchain.BlockchainValidateDTO;
import com.pefoce.challenge_pefoce.dto.blockchain.CadeiaCustodiaTransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.repository.TransferenciaRepository;
import com.pefoce.challenge_pefoce.service.blockchain.BlockchainValidateTransferencia;
import com.pefoce.challenge_pefoce.service.transferencia.TransferenciaMapper;
import com.pefoce.challenge_pefoce.service.vestigio.VestigioQueryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RelatorioTransferenciaVestigioService {

  private final VestigioQueryService vestigioQueryService;
  private final TransferenciaRepository transferenciaRepository;
  private final TransferenciaMapper transferenciaMapper;
  private final BlockchainValidateTransferencia validateTransferenciaService;


  public RelatorioTransferenciaVestigioService(VestigioQueryService vestigioQueryService,
                                               TransferenciaRepository transferenciaRepository,
                                               TransferenciaMapper transferenciaMapper,
                                               BlockchainValidateTransferencia validateTransferenciaService) {
    this.vestigioQueryService = vestigioQueryService;
    this.transferenciaRepository = transferenciaRepository;
    this.transferenciaMapper = transferenciaMapper;
    this.validateTransferenciaService = validateTransferenciaService;
  }

  @Cacheable(value = "relatorios", key = "#vestigioId")
  @Transactional(readOnly = true)
  public CadeiaCustodiaTransferenciaDTO gerarCadeiaDeCustodia(UUID vestigioId) {
    VestigioDTO vestigioDTO = vestigioQueryService.buscarPorId(vestigioId);
    List<Transferencia> transferencias = transferenciaRepository.findByVestigios_IdOrderByDataTransferenciaAsc(vestigioId);

    BlockchainValidateDTO statusIntegridade = validateTransferenciaService.validarHistorico(transferencias);

    List<TransferenciaDTO> historicoDeTransferenciasDTO = transferencias.stream()
      .map(transferenciaMapper::toDTO)
      .collect(Collectors.toList());

    return new CadeiaCustodiaTransferenciaDTO(vestigioDTO, statusIntegridade, historicoDeTransferenciasDTO);
  }
}