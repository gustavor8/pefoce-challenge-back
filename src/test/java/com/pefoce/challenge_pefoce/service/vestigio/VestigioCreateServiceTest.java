package com.pefoce.challenge_pefoce.service.vestigio;

import com.pefoce.challenge_pefoce.dto.vestigio.VestigioCreateDTO;
import com.pefoce.challenge_pefoce.dto.vestigio.VestigioDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.entity.vestigio.StatusVestigio;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VestigioCreateServiceTest {
  @Mock
  private VestigioRepository vestigioRepository;
  @Mock
  private UsuarioRepository usuarioRepository;
  @Mock
  private VestigioMapper vestigioMapper;
  @InjectMocks
  private VestigioCreateService vestigioCreateService;

  @Test
  @DisplayName("Deve criar um vestígio com sucesso quando o usuário responsável existe")
  void criarVestigio_comDadosValidos_deveCriarEVestornarVestigioDTO() {
    UUID responsavelId = UUID.randomUUID();
    VestigioCreateDTO createDTO = new VestigioCreateDTO("Celular", "iPhone 15 Pro", "Cena do Crime A", OffsetDateTime.now(), responsavelId);
    Usuario responsavelMock = new Usuario();
    responsavelMock.setId(responsavelId);
    Vestigio vestigioSalvoMock = new Vestigio();
    VestigioDTO vestigioDTOMock = mock(VestigioDTO.class);
    when(usuarioRepository.findById(responsavelId)).thenReturn(Optional.of(responsavelMock));
    when(vestigioRepository.save(any(Vestigio.class))).thenReturn(vestigioSalvoMock);
    when(vestigioMapper.toDTO(vestigioSalvoMock)).thenReturn(vestigioDTOMock);
    VestigioDTO resultado = vestigioCreateService.criarVestigio(createDTO);
    assertNotNull(resultado);
    assertEquals(vestigioDTOMock, resultado);
    ArgumentCaptor<Vestigio> vestigioCaptor = ArgumentCaptor.forClass(Vestigio.class);
    verify(vestigioRepository).save(vestigioCaptor.capture());
    Vestigio vestigioCapturado = vestigioCaptor.getValue();
    assertEquals("Celular", vestigioCapturado.getTipo());
    assertEquals("iPhone 15 Pro", vestigioCapturado.getDescricao());
    assertEquals(responsavelMock, vestigioCapturado.getResponsavelAtual());
    assertEquals(StatusVestigio.COLETADO, vestigioCapturado.getStatus());
    verify(usuarioRepository, times(1)).findById(responsavelId);
    verify(vestigioRepository, times(1)).save(any(Vestigio.class));
    verify(vestigioMapper, times(1)).toDTO(vestigioSalvoMock);
  }

  @Test
  @DisplayName("Deve lançar EntityNotFoundException quando o usuário responsável não é encontrado")
  void criarVestigio_comUsuarioInexistente_deveLancarEntityNotFoundException() {
    UUID responsavelIdInexistente = UUID.randomUUID();
    VestigioCreateDTO createDTO = new VestigioCreateDTO("Celular", "iPhone 15 Pro", "Cena do Crime A", OffsetDateTime.now(), responsavelIdInexistente);
    when(usuarioRepository.findById(responsavelIdInexistente)).thenReturn(Optional.empty());
EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
      () -> vestigioCreateService.criarVestigio(createDTO));
    assertEquals("Usuário responsável não encontrado.", exception.getMessage());
    verify(vestigioRepository, never()).save(any());
    verify(vestigioMapper, never()).toDTO(any());
  }
}
