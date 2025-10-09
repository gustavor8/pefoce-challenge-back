package com.pefoce.challenge_pefoce.service.vestigio;

import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioUpdateDTO;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VestigioUpdateServiceTest {
  @Mock
  private VestigioRepository vestigioRepository;
  @Mock
  private VestigioMapper vestigioMapper;
  @InjectMocks
  private VestigioUpdateService vestigioUpdateService;

  @Test
  @DisplayName("Deve atualizar o vestígio com sucesso quando o ID existe")
  void atualizarVestigio_quandoVestigioExiste_deveAtualizarEretornarDTO() {
    UUID vestigioId = UUID.randomUUID();
    VestigioUpdateDTO updateDTO = new VestigioUpdateDTO("Novo Tipo", "Nova Descrição", "Novo Local", "EM_ANALISE");
    Vestigio vestigioExistente = new Vestigio();
    vestigioExistente.setId(vestigioId);
    vestigioExistente.setTipo("Tipo Antigo");
    VestigioDTO vestigioDTOMock = mock(VestigioDTO.class);
    when(vestigioRepository.findById(vestigioId)).thenReturn(Optional.of(vestigioExistente));
    when(vestigioRepository.save(any(Vestigio.class))).thenReturn(vestigioExistente); // O save retorna a entidade atualizada
    when(vestigioMapper.toDTO(vestigioExistente)).thenReturn(vestigioDTOMock);
    VestigioDTO resultado = vestigioUpdateService.atualizarVestigio(vestigioId, updateDTO);
    assertNotNull(resultado);
    assertEquals(vestigioDTOMock, resultado);
    ArgumentCaptor<Vestigio> vestigioCaptor = ArgumentCaptor.forClass(Vestigio.class);
    verify(vestigioRepository).save(vestigioCaptor.capture());
    Vestigio vestigioCapturado = vestigioCaptor.getValue();
    assertEquals("Novo Tipo", vestigioCapturado.getTipo());
    assertEquals("Nova Descrição", vestigioCapturado.getDescricao());
    assertEquals("Novo Local", vestigioCapturado.getLocalColeta());
  }

  @Test
  @DisplayName("Deve lançar EntityNotFoundException quando o vestígio a ser atualizado não existe")
  void atualizarVestigio_quandoVestigioNaoExiste_deveLancarEntityNotFoundException() {
    UUID vestigioId = UUID.randomUUID();
    VestigioUpdateDTO updateDTO = new VestigioUpdateDTO("Tipo", "Desc", "Local", "DESCARTADO");
    when(vestigioRepository.findById(vestigioId)).thenReturn(Optional.empty());
    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
      () -> vestigioUpdateService.atualizarVestigio(vestigioId, updateDTO));
    assertEquals("Vestígio não encontrado com o ID: " + vestigioId, exception.getMessage());
    verify(vestigioRepository, never()).save(any());
    verify(vestigioMapper, never()).toDTO(any());
  }
}