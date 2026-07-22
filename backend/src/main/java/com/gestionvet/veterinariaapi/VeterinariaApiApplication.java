package com.gestionvet.veterinariaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync  // habilita @Async para el registro asíncrono de auditoría
public class VeterinariaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VeterinariaApiApplication.class, args);
    }
}
