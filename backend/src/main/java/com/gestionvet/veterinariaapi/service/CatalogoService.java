package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.RolDTO;
import com.gestionvet.veterinariaapi.entity.*;
import com.gestionvet.veterinariaapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para las tablas de catálogo (solo lectura en esta fase):
 * Roles, Permisos, Especialidades, Especies, Razas, TiposCita.
 */
@Service
@Transactional(readOnly = true)
public class CatalogoService {

    @Autowired private RolRepository rolRepository;
    @Autowired private EspecieRepository especieRepository;
    @Autowired private RazaRepository razaRepository;
    @Autowired private EspecialidadRepository especialidadRepository;
    @Autowired private TipoCitaRepository tipoCitaRepository;

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public List<RolDTO> listarRolesDTO() {
        return rolRepository.findAll().stream()
                .map(r -> new RolDTO(r.getId(), r.getNombre(), r.getDescripcion(), null))
                .toList();
    }

    public List<Especie> listarEspecies() {
        return especieRepository.findAll();
    }

    public List<Raza> listarRazas() {
        return razaRepository.findAll();
    }

    public List<Raza> listarRazasPorEspecie(Integer especieId) {
        return razaRepository.findByEspecieId(especieId);
    }

    public List<Especialidad> listarEspecialidades() {
        return especialidadRepository.findAll();
    }

    public List<TipoCita> listarTiposCita() {
        return tipoCitaRepository.findAll();
    }
}
