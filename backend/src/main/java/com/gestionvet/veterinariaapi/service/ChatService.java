package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.ChatResponseDTO;
import com.gestionvet.veterinariaapi.repository.CitaRepository;
import com.gestionvet.veterinariaapi.repository.ClienteRepository;
import com.gestionvet.veterinariaapi.repository.MascotaRepository;
import com.gestionvet.veterinariaapi.repository.MedicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    @Autowired private CitaRepository    citaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private MascotaRepository mascotaRepository;
    @Autowired private MedicoRepository  medicoRepository;

    @Value("${ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${ollama.model:llama3.2:3b}")
    private String ollamaModel;

    private final RestTemplate restTemplate = new RestTemplate();

    // ── Punto de entrada ──────────────────────────────────────────────────

    public ChatResponseDTO consultar(String pregunta) {
        try {
            String systemPrompt = construirSystemPrompt();
            String respuesta    = llamarOllama(systemPrompt, pregunta);
            return new ChatResponseDTO(respuesta, true);
        } catch (Exception e) {
            log.error("Error en ChatService: {}", e.getMessage());
            return new ChatResponseDTO(
                "No pude conectarme con el asistente. " +
                "Verifica que Ollama esté corriendo con: ollama serve", false);
        }
    }

    // ── Construir system prompt con datos reales de la BD ─────────────────

    private String construirSystemPrompt() {
        LocalDate hoy = LocalDate.now();
        String fechaFormateada = hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Consultas rápidas de conteo — todas read-only
        long citasHoy       = citaRepository.countByFecha(hoy);
        long citasPendientes= citaRepository.countByFechaAndEstado(hoy, "pendiente");
        long citasEnCurso   = citaRepository.countByFechaAndEstado(hoy, "en_curso");
        long citasCompletadas=citaRepository.countByFechaAndEstado(hoy, "completada");

        long clientesActivos    = clienteRepository.countByEstado("activo");
        long mascotasActivas    = mascotaRepository.countByEstado("activo");
        long medicosDisponibles = medicoRepository.countByDisponibleTrue();

        return """
            Eres un asistente amigable de una clínica veterinaria llamada VetGest.
            Responde siempre en español, de forma concisa y natural.
            Si el usuario saluda, responde el saludo de forma amigable.
            Si pregunta sobre datos del sistema, usa la información de contexto que tienes a continuación.
            Si la pregunta no tiene relación con la veterinaria, responde de todas formas de forma útil y amigable.

            === DATOS DEL SISTEMA HOY (%s) ===
            - Citas de hoy: %d (pendientes: %d, en curso: %d, completadas: %d)
            - Clientes activos: %d
            - Mascotas activas: %d
            - Médicos disponibles: %d
            =====================================
            """.formatted(
                fechaFormateada,
                citasHoy, citasPendientes, citasEnCurso, citasCompletadas,
                clientesActivos,
                mascotasActivas,
                medicosDisponibles
            );
    }

    // ── Llamada directa a Ollama ──────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String llamarOllama(String systemPrompt, String pregunta) {
        Map<String, Object> body = new HashMap<>();
        body.put("model",  ollamaModel);
        body.put("system", systemPrompt);
        body.put("prompt", pregunta);
        body.put("stream", false);

        Map<String, Object> response = restTemplate.postForObject(
            ollamaUrl + "/api/generate", body, Map.class);

        if (response == null) {
            throw new RuntimeException("Respuesta vacía de Ollama");
        }

        String respuesta = (String) response.getOrDefault("response", "");
        if (respuesta.isBlank()) {
            throw new RuntimeException("El modelo no generó respuesta");
        }

        return respuesta.trim();
    }
}
