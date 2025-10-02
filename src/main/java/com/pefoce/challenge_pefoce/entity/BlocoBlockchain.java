package com.pefoce.challenge_pefoce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "blockchain")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlocoBlockchain {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private Long numeroBloco;
  private String hashAnterior;
  private String hashAtual;

  @Column(columnDefinition = "TEXT")
  private String dados;
  private LocalDateTime carimboDeTempo;
  private Integer nonce;

  @PrePersist
  public void onPrePersist() {
    this.carimboDeTempo = LocalDateTime.now();
  }
}