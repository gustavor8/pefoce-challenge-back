package com.pefoce.challenge_pefoce.entity.vestigio;

import com.pefoce.challenge_pefoce.entity.Transferencia;
import com.pefoce.challenge_pefoce.entity.Users;
import com.pefoce.challenge_pefoce.entity.VestigioHistorico;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString(of = {"id", "motivo", "dataTransferencia"}) // Incluí somente esses pra não dar erro
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vestigios")
public class Vestigio {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String tipo;

  @Column(columnDefinition = "TEXT")
  private String descricao;

  @Column(name = "local_coleta")
  private String localColeta;

  @Column(name = "data_coleta", nullable = false)
  private OffsetDateTime dataColeta;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatusVestigio status;


  @CreationTimestamp
  @Column(name = "criado_em", nullable = false, updatable = false)
  private OffsetDateTime criadoEm;

  @UpdateTimestamp
  @Column(name = "atualizado_em", nullable = false)
  private OffsetDateTime atualizadoEm;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "responsavel_atual_id", nullable = false)
  private Users responsavelAtual;

  @ManyToMany(mappedBy = "vestigios")
  @Builder.Default
  private Set<Transferencia> transferencias = new HashSet<>();

  @OneToMany(mappedBy = "vestigio", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<VestigioHistorico> historico = new HashSet<>();
}