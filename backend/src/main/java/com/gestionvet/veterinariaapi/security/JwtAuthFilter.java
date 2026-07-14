package com.gestionvet.veterinariaapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Leer el header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (quitar el prefijo "Bearer ")
        final String jwt = authHeader.substring(7);

        try {
            // 4. Extraer username del token
            final String username = jwtUtil.extractUsername(jwt);

            // 5. Solo procesar si hay username y el contexto no tiene autenticación previa
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Cargar el usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 7. Validar el token contra el usuario cargado
                if (jwtUtil.isTokenValid(jwt, userDetails)) {

                    // 8. Crear objeto de autenticación y guardarlo en el SecurityContext
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token inválido o expirado — continuar sin autenticar (Spring devuelve 401)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
