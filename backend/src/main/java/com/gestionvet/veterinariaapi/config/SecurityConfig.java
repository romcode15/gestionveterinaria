package com.gestionvet.veterinariaapi.config;

import com.gestionvet.veterinariaapi.security.JwtAuthFilter;
import com.gestionvet.veterinariaapi.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)   // habilita @PreAuthorize en controllers
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // ── Rutas públicas (sin token) ─────────────────────────────────────────

    private static final String[] PUBLIC_URLS = {
        "/api/auth/login",       // solo el login es público
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/api-docs/**",
        "/v3/api-docs/**"
    };

    // ── Cadena de filtros de seguridad ────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (API REST stateless, no usa cookies de sesión)
            .csrf(AbstractHttpConfigurer::disable)

            // Configurar CORS (ya lo maneja CorsConfig.java)
            .cors(cors -> {})

            // Reglas de autorización por endpoint
            .authorizeHttpRequests(auth -> auth

                // ── Rutas completamente públicas ───────────────────────────
                .requestMatchers(PUBLIC_URLS).permitAll()

                // ── Catálogos: cualquier usuario autenticado puede leer ────
                .requestMatchers(HttpMethod.GET, "/api/catalogos/**").authenticated()

                // ── Clientes ───────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/clientes/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/clientes").hasAnyRole("ADMIN", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.PUT,    "/api/clientes/**").hasAnyRole("ADMIN", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").hasRole("ADMIN")

                // ── Mascotas ───────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/mascotas/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/mascotas").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.PUT,    "/api/mascotas/**").hasAnyRole("ADMIN", "VETERINARIO")
                .requestMatchers(HttpMethod.DELETE, "/api/mascotas/**").hasRole("ADMIN")

                // ── Médicos ────────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/medicos/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/medicos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/medicos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/medicos/**").hasRole("ADMIN")

                // ── Citas ──────────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/citas/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/citas").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.PUT,    "/api/citas/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.PATCH,  "/api/citas/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.DELETE, "/api/citas/**").hasRole("ADMIN")

                // ── Portal Cliente (solo rol CLIENTE) ─────────────────────
                .requestMatchers("/api/portal/cliente/**").hasRole("CLIENTE")

                // ── Portal Médico (solo rol VETERINARIO) ───────────────────
                .requestMatchers("/api/portal/medico/**").hasRole("VETERINARIO")

                // ── Dashboard ──────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/dashboard/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")

                // ── Módulo Clínico ─────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/diagnosticos/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/diagnosticos").hasAnyRole("ADMIN", "VETERINARIO")
                .requestMatchers(HttpMethod.PUT,    "/api/diagnosticos/**").hasAnyRole("ADMIN", "VETERINARIO")

                .requestMatchers(HttpMethod.GET,    "/api/tratamientos/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/tratamientos").hasAnyRole("ADMIN", "VETERINARIO")
                .requestMatchers(HttpMethod.PUT,    "/api/tratamientos/**").hasAnyRole("ADMIN", "VETERINARIO")

                .requestMatchers(HttpMethod.GET,    "/api/historial-clinico/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")

                .requestMatchers(HttpMethod.GET,    "/api/vias-administracion/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/vias-administracion").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/vias-administracion/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/vias-administracion/**").hasRole("ADMIN")

                // ── Inventario ─────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/inventario/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/inventario/categorias").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/inventario/categorias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/inventario/proveedores").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/inventario/proveedores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/inventario/proveedores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/inventario/productos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/inventario/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/inventario/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/inventario/lotes").hasAnyRole("ADMIN", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/inventario/movimientos/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")

                // ── Vacunación ─────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/vacunas/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/vacunas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/vacunas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/vacunas/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET,    "/api/mascota-vacunas/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.GET,    "/api/mascota-vacunas").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST,   "/api/mascota-vacunas").hasAnyRole("ADMIN", "VETERINARIO")
                .requestMatchers(HttpMethod.PUT,    "/api/mascota-vacunas/**").hasAnyRole("ADMIN", "VETERINARIO")

                // ── Chat IA (solo ADMIN) ───────────────────────────────────
                .requestMatchers("/api/chat/**").hasRole("ADMIN")

                // ── Auditoría (solo ADMIN) ─────────────────────────────────
                .requestMatchers("/api/auditoria/**").hasRole("ADMIN")

                // ── Usuarios (solo ADMIN) ──────────────────────────────────
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // ── Recepcionistas (solo ADMIN) ────────────────────────────
                .requestMatchers("/api/recepcionistas/**").hasRole("ADMIN")

                // ── Cambio de contraseña (cualquier usuario autenticado) ───
                .requestMatchers(HttpMethod.PATCH, "/api/auth/cambiar-password").authenticated()

                // ── Cualquier otra ruta requiere autenticación ─────────────
                .anyRequest().authenticated()
            )

            // Sin sesión HTTP (JWT es stateless)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Proveedor de autenticación con BCrypt
            .authenticationProvider(authenticationProvider())

            // Registrar el filtro JWT antes del filtro de autenticación por usuario/password
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── Beans de infraestructura de seguridad ──────────────────────────────

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);  // factor de coste 10 (recomendado)
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }}
