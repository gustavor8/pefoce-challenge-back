package com.pefoce.challenge_pefoce.util;


import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaCreateDTO;
import com.pefoce.challenge_pefoce.entity.Usuario;
import com.pefoce.challenge_pefoce.entity.vestigio.StatusVestigio;
import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import com.pefoce.challenge_pefoce.repository.UsuarioRepository;
import com.pefoce.challenge_pefoce.repository.VestigioRepository;
import com.pefoce.challenge_pefoce.service.transferencia.TransferenciaCreateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;


@Component
@Profile("dev")
public class DataSeeder implements ApplicationListener<ApplicationReadyEvent> {

  private final UsuarioRepository usuarioRepository;
  private final VestigioRepository vestigioRepository;
  private final PasswordEncoder passwordEncoder;

  private final TransferenciaCreateService transferenciaCreateService;
  private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

  public DataSeeder(UsuarioRepository usuarioRepository, VestigioRepository vestigioRepository,
                    PasswordEncoder passwordEncoder, TransferenciaCreateService transferenciaCreateService) {
    this.usuarioRepository = usuarioRepository;
    this.vestigioRepository = vestigioRepository;
    this.passwordEncoder = passwordEncoder;
    this.transferenciaCreateService = transferenciaCreateService;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    if (usuarioRepository.findByUsername("enio.perito").isPresent() || usuarioRepository.findByUsername("emanuel.perito").isPresent()) {
      log.info("Usuários de seed já existem no banco. Ignorando a população de dados.");
      return;
    }
    log.info("Populando o banco de dados com dados iniciais...");

    Usuario userEmanuel = Usuario.builder()
      .username("emanuel.perito")
      .password(passwordEncoder.encode("senha123"))
      .nome("Emanuel Félix")
      .email("emanuel.perito@gmail.com")
      .cargo("Perito Criminal")
      .departamento("COPEC")
      .ativo(true)
      .build();

    Usuario userEnio = Usuario.builder()
      .username("enio.perito")
      .password(passwordEncoder.encode("senha123"))
      .nome("Ênio Viana")
      .email("enio.perito@gmail.com")
      .cargo("Perito Criminal")
      .departamento("NPTAT")
      .ativo(true)
      .build();

    usuarioRepository.saveAll(List.of(userEmanuel, userEnio));

    Vestigio celular = Vestigio.builder()
      .tipo("Aparelho Celular")
      .descricao("Smartphone Samsung A51, tela trincada")
      .localColeta("Rua das Flores, 123")
      .dataColeta(OffsetDateTime.now().minusDays(10))
      .status(StatusVestigio.COLETADO)
      .responsavelAtual(userEmanuel)
      .build();

    Vestigio notebook = Vestigio.builder()
      .tipo("Notebook")
      .descricao("Notebook Dell Inspiron, cor prata")
      .localColeta("Av. Principal, 456")
      .dataColeta(OffsetDateTime.now().minusDays(9))
      .status(StatusVestigio.COLETADO)
      .responsavelAtual(userEmanuel)
      .build();

    Vestigio documento = Vestigio.builder()
      .tipo("Documento Físico")
      .descricao("Contrato social com assinaturas")
      .localColeta("Prédio Comercial, sala 302")
      .dataColeta(OffsetDateTime.now().minusDays(8))
      .status(StatusVestigio.COLETADO)
      .responsavelAtual(userEmanuel)
      .build();

    Vestigio arma = Vestigio.builder()
      .tipo("Arma de Fogo")
      .descricao("Pistola Taurus G2C 9mm")
      .localColeta("Depósito abandonado, zona industrial")
      .dataColeta(OffsetDateTime.now().minusDays(7))
      .status(StatusVestigio.COLETADO)
      .responsavelAtual(userEnio)
      .build();

    Vestigio substancia = Vestigio.builder()
      .tipo("Substância Química")
      .descricao("Pó branco em saco plástico zip-lock")
      .localColeta("Laboratório clandestino, Bairro Messejana")
      .dataColeta(OffsetDateTime.now().minusDays(6))
      .status(StatusVestigio.COLETADO)
      .responsavelAtual(userEnio)
      .build();

    vestigioRepository.saveAll(List.of(celular, notebook, documento, arma, substancia));

    TransferenciaCreateDTO t1_dto = new TransferenciaCreateDTO(Set.of(celular.getId()), userEnio.getId(), "Encaminhado para perícia de extração de dados");
    transferenciaCreateService.criar(t1_dto, userEmanuel);

    TransferenciaCreateDTO t2_dto = new TransferenciaCreateDTO(Set.of(notebook.getId()), userEnio.getId(), "Análise de disco rígido");
    transferenciaCreateService.criar(t2_dto, userEmanuel);

    TransferenciaCreateDTO t3_dto = new TransferenciaCreateDTO(Set.of(celular.getId()), userEmanuel.getId(), "Perícia de extração de dados concluída. Devolvendo.");
    transferenciaCreateService.criar(t3_dto, userEnio);

    TransferenciaCreateDTO t4_dto = new TransferenciaCreateDTO(Set.of(arma.getId(), substancia.getId()), userEmanuel.getId(), "Encaminhado para análise balística e química");
    transferenciaCreateService.criar(t4_dto, userEnio);

    TransferenciaCreateDTO t5_dto = new TransferenciaCreateDTO(Set.of(documento.getId()), userEnio.getId(), "Análise de grafoscopia");
    transferenciaCreateService.criar(t5_dto, userEmanuel);

    log.info("Adicionado os peritos:user: emanuel.perito  password: senha123 user: enio.perito  password: senha123");
    log.info("Banco de dados populado com sucesso.");
  }
}