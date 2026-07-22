package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.HistorialClinicoDTO;
import com.gestionvet.veterinariaapi.service.HistorialClinicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historial-clinico")
@Tag(name = "Historial Clínico", description = "Vista consolidada del historial médico de una mascota")
@SecurityRequirement(name = "bearerAuth")
public class HistorialClinicoController {

    @Autowired
    private HistorialClinicoService service;

    @GetMapping("/mascota/{mascotaId}")
    @Operation(summary = "Obtener historial clínico completo de una mascota",
               description = "Devuelve todas las consultas con diagnóstico y tratamiento, " +
                             "ordenadas de más reciente a más antigua.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial obtenido"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<HistorialClinicoDTO> obtenerPorMascota(@PathVariable Integer mascotaId) {
        return ResponseEntity.ok(service.obtenerPorMascota(mascotaId));
    }
}
