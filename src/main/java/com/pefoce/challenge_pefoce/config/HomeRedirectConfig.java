package com.pefoce.challenge_pefoce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry; // registra controladores de view
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // Configuração do Spring Web MVC.

@Configuration
public class HomeRedirectConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/", "/swagger-ui.html");
  }
}