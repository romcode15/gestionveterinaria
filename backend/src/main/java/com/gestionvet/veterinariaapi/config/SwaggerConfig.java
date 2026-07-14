package com.gestionvet.veterinariaapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    // Nombre del esquema de seguridad — debe coincidir con
    // @SecurityRequirement(name = "bearerAuth") en los controllers
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI veterinariaOpenAPI() {
        return new OpenAPI()

            // ── Información general de la API ──────────────────────────────
            .info(new Info()
                .title("GestionVet API")
                .description("""
                    ## API REST — Sistema de Gestión Veterinaria
                    
                    ### Cómo autenticarse:
                    1. Ejecutar **POST /api/auth/login** con `username` y `password`
                    2. Copiar el valor del campo `token` de la respuesta
                    3. Click en el botón **Authorize 🔒** (arriba a la derecha)
                    4. Ingresar: `Bearer <token>` y confirmar
                    5. Todos los endpoints protegidos ya incluirán el token automáticamente
                    
                    ### Credenciales de prueba:
                    | Usuario | Contraseña | Rol |
                    |---------|-----------|-----|
                    | admin | admin123 | Administrador |
                    
                    ### Roles disponibles:
                    - **ADMIN** — acceso total
                    - **VETERINARIO** — consultas, mascotas, citas
                    - **RECEPCIONISTA** — clientes, mascotas, citas
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("GestionVet")
                    .email("admin@gestionvet.com"))
                .license(new License()
                    .name("Proyecto Académico")
                    .url("https://github.com/romcode15/gestionveterinaria"))
            )

            // ── Servidor base ──────────────────────────────────────────────
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Servidor local de desarrollo")
            ))

            // ── Esquema de seguridad JWT (Bearer Token) ────────────────────
            .components(new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Ingresa el token JWT obtenido de POST /api/auth/login. " +
                                     "Formato: Bearer <token>")
                )
            )

            // ── Aplicar seguridad globalmente a todos los endpoints ─────────
            // Los endpoints de /api/auth/** la ignoran porque están como permitAll()
            .addSecurityItem(new SecurityRequirement()
                .addList(SECURITY_SCHEME_NAME)
            );
    }
}
