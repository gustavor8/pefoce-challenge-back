package com.pefoce.challenge_pefoce.entity;

import com.pefoce.challenge_pefoce.entity.vestigio.Vestigio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vestigio_historico")
public class VestigioHistorico {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @Column(name = "descricao_modificacao", nullable = false, columnDefinition = "TEXT")
  private String descricaoModificacao;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vestigio_id", nullable = false)
  private Vestigio vestigio;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_responsavel_id")
  private Users usuarioResponsavel;
  @CreationTimestamp
  @Column(name = "data_modificacao", nullable = false, updatable = false)
  private OffsetDateTime dataModificacao;
}