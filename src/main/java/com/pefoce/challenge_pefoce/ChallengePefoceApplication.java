package com.pefoce.challenge_pefoce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;


import com.pefoce.challenge_pefoce.config.RedisCacheConfig; // 1. Adicione este import

@EnableCaching
@SpringBootApplication
@Import(RedisCacheConfig.class)
public class ChallengePefoceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChallengePefoceApplication.class, args);
  }
}