package com.pefoce.challenge_pefoce.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "blockchain")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlocoBlockchain {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private Long numeroBloco;
  private String hashAnterior;
  private String hashAtual;

  @OneToMany(mappedBy = "blocoBlockchain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Transferencia> transacoes = new HashSet<>();

  private LocalDateTime carimboDeTempo;

}