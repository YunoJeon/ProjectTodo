package com.todo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {

    return new OpenAPI()
        .components(new Components())
        .info(apiInfo());
  }

  private Info apiInfo() {

    return new Info()
        .title("To-Do - 할일 정리 & 협업 도구")
        .description("개인적인 할일 정리와 업무 효율성을 올리기 위한 개인 및 협업 툴")
        .version("1.0.0");
  }
}