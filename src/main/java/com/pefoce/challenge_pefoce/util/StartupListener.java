package com.pefoce.challenge_pefoce.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);
  private final Environment environment;

  public StartupListener(Environment environment) {
    this.environment = environment;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    String port = environment.getProperty("local.server.port");

    LOGGER.info("------------------------------------------------------------------");
    LOGGER.info("🚀 API iniciada com sucesso!");
    LOGGER.info("   A API está rodando no endereço: http://localhost:{}", port);
    LOGGER.info("------------------------------------------------------------------");
  }
}