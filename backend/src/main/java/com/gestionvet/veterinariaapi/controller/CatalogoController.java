package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.entity.*;
import com.gestionvet.veterinariaapi.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@Tag(name = "Catálogos", description = "Tablas de referencia: roles, especies, razas, especialidades, tipos de cita")
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;

    // ── Roles ──────────────────────────────────────────────────────────────

    @GetMapping("/roles")
    @Operation(summary = "Listar todos los roles del sistema")
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(catalogoService.listarRoles());
    }

    // ── Especies ───────────────────────────────────────────────────────────

    @GetMapping("/especies")
    @Operation(summary = "Listar todas las especies animales")
    public ResponseEntity<List<Especie>> listarEspecies() {
        return ResponseEntity.ok(catalogoService.listarEspecies());
    }

    // ── Razas ──────────────────────────────────────────────────────────────

    @GetMapping("/razas")
    @Operation(summary = "Listar todas las razas")
    public ResponseEntity<List<Raza>> listarRazas() {
        return ResponseEntity.ok(catalogoService.listarRazas());
    }

    @GetMapping("/razas/especie/{especieId}")
    @Operation(summary = "Listar razas filtradas por especie")
    public ResponseEntity<List<Raza>> listarRazasPorEspecie(@PathVariable Integer especieId) {
        return ResponseEntity.ok(catalogoService.listarRazasPorEspecie(especieId));
    }

    // ── Especialidades ─────────────────────────────────────────────────────

    @GetMapping("/especialidades")
    @Operation(summary = "Listar todas las especialidades médicas")
    public ResponseEntity<List<Especialidad>> listarEspecialidades() {
        return ResponseEntity.ok(catalogoService.listarEspecialidades());
    }

    // ── Tipos de Cita ──────────────────────────────────────────────────────

    @GetMapping("/tipos-cita")
    @Operation(summary = "Listar todos los tipos de cita con su duración y color")
    public ResponseEntity<List<TipoCita>> listarTiposCita() {
        return ResponseEntity.ok(catalogoService.listarTiposCita());
    }
}
