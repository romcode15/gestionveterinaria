package com.gestionvet.veterinariaapi.config;

import com.gestionvet.veterinariaapi.entity.Rol;
import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.repository.RolRepository;
import com.gestionvet.veterinariaapi.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Se ejecuta automáticamente al arrancar la aplicación.
 * Crea el usuario administrador si no existe en la base de datos.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Lee los valores desde application.properties (que los toma de .env)
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        crearAdminSiNoExiste();
    }

    private void crearAdminSiNoExiste() {

        // Verificar si el admin ya existe
        Optional<Usuario> adminExistente = usuarioRepository.findByUsername(adminUsername);

        if (adminExistente.isPresent()) {
            log.info("✅ Usuario admin ya existe — no se crea de nuevo (username: {})", adminUsername);
            return;
        }

        // Buscar el rol 'admin' en la BD
        Optional<Rol> rolAdmin = rolRepository.findByNombre("admin");

        if (rolAdmin.isEmpty()) {
            log.warn("⚠️  Rol 'admin' no encontrado en la BD. " +
                     "Asegúrate de haber ejecutado el script SQL con los datos base.");
            return;
        }

        // Crear el usuario admin con password hasheado con BCrypt
        Usuario admin = new Usuario();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setEmail(adminEmail);
        admin.setNombre("Admin");
        admin.setApellido("Sistema");
        admin.setActivo(true);

        Set<Rol> roles = new HashSet<>();
        roles.add(rolAdmin.get());
        admin.setRoles(roles);

        usuarioRepository.save(admin);

        log.info("✅ Usuario administrador creado exitosamente:");
        log.info("   Username : {}", adminUsername);
        log.info("   Email    : {}", adminEmail);
        log.info("   Password : [protegida con BCrypt]");
        log.info("   Rol      : admin");
    }
}
