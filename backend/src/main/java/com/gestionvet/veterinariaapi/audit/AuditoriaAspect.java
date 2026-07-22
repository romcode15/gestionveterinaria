package com.gestionvet.veterinariaapi.audit;

import com.gestionvet.veterinariaapi.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * Aspecto que intercepta automáticamente todos los métodos de los controladores
 * REST y registra la auditoría sin modificar ningún código existente.
 *
 * Captura:
 * - Quién: usuario autenticado (username + id)
 * - Qué: método HTTP + nombre del controlador + resultado
 * - Dónde: IP de origen + endpoint
 * - Cuándo: manejado por la entidad AuditoriaGeneral (@PrePersist)
 * - Resultado: exitoso o error con mensaje
 */
@Aspect
@Component
public class AuditoriaAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaAspect.class);

    @Autowired
    private AuditoriaService auditoriaService;

    /**
     * Intercepta todos los métodos públicos de todos los @RestController.
     * Solo audita operaciones de escritura (POST, PUT, PATCH, DELETE)
     * para evitar llenar la tabla con miles de GETs de lectura.
     */
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object auditarControlador(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        String nombreMetodo = method.getName();

        // Solo auditar escrituras
        boolean esEscritura = nombreMetodo.startsWith("crear")
                || nombreMetodo.startsWith("registrar")
                || nombreMetodo.startsWith("actualizar")
                || nombreMetodo.startsWith("eliminar")
                || nombreMetodo.startsWith("desactivar")
                || nombreMetodo.startsWith("cambiarEstado")
                || nombreMetodo.startsWith("salidaManual")
                || nombreMetodo.startsWith("login");

        if (!esEscritura) {
            return pjp.proceed();
        }

        // ── Obtener contexto HTTP ──────────────────────────────────────────
        String ip       = obtenerIp();
        String endpoint = obtenerEndpoint();

        // ── Obtener usuario autenticado ────────────────────────────────────
        String  username  = "anonimo";
        Integer usuarioId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            username = auth.getName();
        }

        // ── Determinar entidad y acción ────────────────────────────────────
        String claseNombre = pjp.getTarget().getClass().getSimpleName()
                .replace("Controller", "");
        String accion      = resolverAccion(nombreMetodo);

        // ── Ejecutar el método original ────────────────────────────────────
        Object resultado = null;
        boolean exitoso  = true;
        String  error    = null;

        try {
            resultado = pjp.proceed();
            return resultado;
        } catch (Throwable t) {
            exitoso = false;
            error   = t.getClass().getSimpleName() + ": " + t.getMessage();
            throw t;
        } finally {
            // Extraer ID del resultado si es posible (para el campo entidad_id)
            String entidadId = extraerEntidadId(resultado);

            String descripcion = username + " ejecutó " + accion
                    + " en " + claseNombre
                    + (entidadId != null ? " [id=" + entidadId + "]" : "");

            // Registro asíncrono — no bloquea la respuesta al cliente
            auditoriaService.registrar(
                    username, usuarioId, accion,
                    claseNombre, entidadId, descripcion,
                    ip, endpoint, exitoso, error);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private String resolverAccion(String nombreMetodo) {
        if (nombreMetodo.startsWith("crear") || nombreMetodo.startsWith("registrar")) return "CREATE";
        if (nombreMetodo.startsWith("actualizar") || nombreMetodo.startsWith("cambiarEstado")) return "UPDATE";
        if (nombreMetodo.startsWith("eliminar") || nombreMetodo.startsWith("desactivar")) return "DELETE";
        if (nombreMetodo.startsWith("login"))    return "LOGIN";
        if (nombreMetodo.startsWith("salida"))   return "SALIDA_INVENTARIO";
        return nombreMetodo.toUpperCase();
    }

    private String obtenerIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest request = attrs.getRequest();
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            return (xForwardedFor != null && !xForwardedFor.isBlank())
                    ? xForwardedFor.split(",")[0].trim()
                    : request.getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }

    private String obtenerEndpoint() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest request = attrs.getRequest();
            return request.getMethod() + " " + request.getRequestURI();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Intenta extraer el ID del objeto devuelto por el controlador.
     * Funciona con ResponseEntity<DTO> donde el DTO tiene getId().
     */
    private String extraerEntidadId(Object resultado) {
        if (resultado == null) return null;
        try {
            // ResponseEntity -> getBody() -> getId()
            Object body = resultado.getClass().getMethod("getBody").invoke(resultado);
            if (body == null) return null;
            Object id = body.getClass().getMethod("getId").invoke(body);
            return id != null ? id.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
