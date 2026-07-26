package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.DashboardDTO;
import com.gestionvet.veterinariaapi.dto.ResumenGeneralDTO;
import com.gestionvet.veterinariaapi.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Estadísticas y resumen operativo de la veterinaria")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    @Autowired
    private DashboardService service;

    @GetMapping("/resumen-general")
    @Operation(
        summary = "Conteos globales para tarjetas del dashboard",
        description = "Devuelve 4 COUNT reales de BD: clientes activos, mascotas activas, médicos disponibles y citas de hoy. Nunca tamaños de página."
    )
    @ApiResponse(responseCode = "200", description = "Resumen obtenido")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<ResumenGeneralDTO> resumenGeneral() {
        return ResponseEntity.ok(service.obtenerResumenGeneral());
    }

    @GetMapping
    @Operation(
        summary = "Obtener dashboard principal",
        description = """
            Devuelve en una sola llamada:
            - Resumen del día: total de citas, estados, mascotas atendidas
            - Estadísticas del período: médico más activo, mascotas más atendidas,
              citas por tipo/estado/día de semana, productos más usados en tratamientos
            - Alertas críticas consolidadas: vacunas vencidas/próximas,
              stock bajo, lotes vencidos/próximos
            """
    )
    @ApiResponse(responseCode = "200", description = "Dashboard generado correctamente")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<DashboardDTO> obtenerDashboard(
            @Parameter(description = "Días hacia atrás para estadísticas del período (default: 30)")
            @RequestParam(defaultValue = "30") int dias,

            @Parameter(description = "Ventana en días para alertas de vacunas (default: 30)")
            @RequestParam(defaultValue = "30") int diasAlertaVacunas,

            @Parameter(description = "Ventana en días para alertas de lotes (default: 30)")
            @RequestParam(defaultValue = "30") int diasAlertaInventario) {

        return ResponseEntity.ok(service.obtenerDashboard(dias, diasAlertaVacunas, diasAlertaInventario));
    }
}
