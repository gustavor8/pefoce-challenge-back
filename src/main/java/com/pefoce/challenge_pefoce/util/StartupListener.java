package com.pefoce.challenge_pefoce.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StartupListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);
  private final Environment environment;

  public StartupListener(Environment environment) {
    this.environment = environment;
  }
  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    String port;
    if (environment.matchesProfiles("docker")) {
      port = "8081";
    } else {
      port = environment.getProperty("local.server.port");
    }
    String[] activeProfiles = environment.getActiveProfiles();
    String profileInfo = (activeProfiles.length > 0) ? Arrays.toString(activeProfiles) : "default";

    LOGGER.info("------------------------------------------------------------------");
    LOGGER.info("🚀 API iniciada com sucesso!");
    LOGGER.info("   Perfil Ativo: {}", profileInfo);
    LOGGER.info("   URL da API: http://localhost:{}", port);
    LOGGER.info("------------------------------------------------------------------");
  }
}