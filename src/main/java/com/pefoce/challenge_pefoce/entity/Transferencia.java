package com.pefoce.challenge_pefoce.entity;

import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transferencias")
public class Transferencia {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "blockchain_id")
  private Blockchain blockchain;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "transferencia_vestigios",
    joinColumns = @JoinColumn(name = "transferencia_id"),
    inverseJoinColumns = @JoinColumn(name = "vestigio_id")
  )
  @Builder.Default
  private Set<Vestigio> vestigios = new HashSet<>();

  @Column(columnDefinition = "TEXT")
  private String motivo;

  @Column(name = "assinatura_digital", columnDefinition = "TEXT")
  private String assinaturaDigital;

  @Column(name = "hash_transacao")
  private String hashTransacao;

  @CreationTimestamp
  @Column(name = "data_transferencia", nullable = false, updatable = false)
  private OffsetDateTime dataTransferencia;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "responsavel_origem_id", nullable = false)
  private Usuario responsavelOrigem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "responsavel_destino_id", nullable = false)
  private Usuario responsavelDestino;
}