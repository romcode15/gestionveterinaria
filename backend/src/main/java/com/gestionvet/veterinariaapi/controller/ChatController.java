package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.ChatRequestDTO;
import com.gestionvet.veterinariaapi.dto.ChatResponseDTO;
import com.gestionvet.veterinariaapi.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat IA", description = "Consultas en lenguaje natural sobre la base de datos — solo ADMIN")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    @Operation(
        summary = "Consultar al asistente IA",
        description = "Envía una pregunta en español. El asistente responde con contexto real de la base de datos."
    )
    public ResponseEntity<ChatResponseDTO> consultar(@Valid @RequestBody ChatRequestDTO request) {
        ChatResponseDTO respuesta = chatService.consultar(request.getPregunta());
        return ResponseEntity.ok(respuesta);
    }
}
