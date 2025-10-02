package com.pefoce.challenge_pefoce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "blockchain")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlocoBlockchain {

  @Id
  private UUID id;

  private Long numeroBloco;
  private String hashAnterior;
  private String hashAtual;

  @Column(columnDefinition = "TEXT")
  private String dados;

  private LocalDateTime carimboDeTempo;

  private Integer nonce;

  @Builder
  public BlocoBlockchain(Long numeroBloco, String hashAnterior, String hashAtual, String dados, Integer nonce) {
    this.id = UUID.randomUUID();
    this.numeroBloco = numeroBloco;
    this.hashAnterior = hashAnterior;
    this.hashAtual = hashAtual;
    this.dados = dados;
    this.nonce = nonce;
    this.carimboDeTempo = LocalDateTime.now();
  }
}