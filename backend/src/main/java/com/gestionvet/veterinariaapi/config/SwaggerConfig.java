package com.gestionvet.veterinariaapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI veterinariaOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("GestionVet API")
                .description("API REST para el Sistema de Gestión Veterinaria. " +
                             "Permite gestionar clientes, mascotas, médicos, citas y usuarios.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("GestionVet")
                    .email("admin@gestionvet.com"))
                .license(new License()
                    .name("Proyecto Académico")
                    .url("https://github.com"))
            );
    }
}
