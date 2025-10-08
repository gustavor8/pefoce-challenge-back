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
public class Blockchain {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private Long numeroBloco;

  @Column(nullable = false, unique = true, length = 256)
  private String hashAnterior;

  @Column(nullable = false, unique = true, length = 256)
  private String hashAtual;

  @OneToMany(mappedBy = "blockchain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Transferencia> transacoes = new HashSet<>();

  private LocalDateTime carimboDeTempo;

}