package com.pefoce.challenge_pefoce.service.vestigio;

import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VestigioDeleteServiceTest {
  @Mock
  private VestigioRepository vestigioRepository;
  @InjectMocks
  private VestigioDeleteService vestigioDeleteService;

  @Test
  @DisplayName("Deve deletar o vestígio com sucesso quando o ID existe")
  void deletarVestigio_quandoVestigioExiste_deveChamarDeleteById() {
    UUID vestigioId = UUID.randomUUID();
    when(vestigioRepository.existsById(vestigioId)).thenReturn(true);
    assertDoesNotThrow(() -> vestigioDeleteService.deletarVestigio(vestigioId));
    verify(vestigioRepository, times(1)).deleteById(vestigioId);
  }

  @Test
  @DisplayName("Deve lançar EntityNotFoundException quando o vestígio não existe")
  void deletarVestigio_quandoVestigioNaoExiste_deveLancarEntityNotFoundException() {
    UUID vestigioId = UUID.randomUUID();
    when(vestigioRepository.existsById(vestigioId)).thenReturn(false);
    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
      () -> vestigioDeleteService.deletarVestigio(vestigioId)
    );
    assertEquals("Vestígio não encontrado com o ID: " + vestigioId, exception.getMessage());
    verify(vestigioRepository, never()).deleteById(any(UUID.class));
  }
}