package com.pefoce.challenge_pefoce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
      .components(new Components().addSecuritySchemes("bearer-key",
        new SecurityScheme()
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT")))
      .info(new Info()
        .title("API Desafio Java Pleno Pefoce")
        .version("v1")
        .description("API desenvolvida para desafio de desenvolvedor Java Pleno")
        .contact(new Contact()
          .name("Gustavo Rodrigues")
          .email("gustavogrmc@gmail.com"))
        .license(new License()
          .name("Apache 2.0")
          .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
      );
  }
}