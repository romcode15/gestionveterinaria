package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.AlertaInventarioDTO;
import com.gestionvet.veterinariaapi.dto.AlertaVacunaDTO;
import com.gestionvet.veterinariaapi.dto.DashboardDTO;
import com.gestionvet.veterinariaapi.dto.DashboardDTO.*;
import com.gestionvet.veterinariaapi.dto.ResumenGeneralDTO;
import com.gestionvet.veterinariaapi.repository.CitaRepository;
import com.gestionvet.veterinariaapi.repository.ClienteRepository;
import com.gestionvet.veterinariaapi.repository.DiagnosticoRepository;
import com.gestionvet.veterinariaapi.repository.MascotaRepository;
import com.gestionvet.veterinariaapi.repository.MedicoRepository;
import com.gestionvet.veterinariaapi.repository.MovimientoInventarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    @Autowired private CitaRepository               citaRepository;
    @Autowired private ClienteRepository            clienteRepository;
    @Autowired private MascotaRepository            mascotaRepository;
    @Autowired private MedicoRepository             medicoRepository;
    @Autowired private DiagnosticoRepository        diagnosticoRepository;
    @Autowired private MovimientoInventarioRepository movimientoRepository;
    @Autowired private MascotaVacunaService         vacunaService;
    @Autowired private InventarioService            inventarioService;

    /**
     * Conteos globales reales para las tarjetas del dashboard.
     * Cada valor es un COUNT directo a BD — nunca un tamaño de página.
     */
    public ResumenGeneralDTO obtenerResumenGeneral() {
        return new ResumenGeneralDTO(
            clienteRepository.countByEstado("activo"),
            mascotaRepository.countByEstado("activo"),
            medicoRepository.countByDisponibleTrue(),
            citaRepository.countByFecha(LocalDate.now())
        );
    }

    /**
     * Dashboard principal.
     * @param diasPeriodo número de días hacia atrás para las estadísticas (default 30)
     * @param diasAlertaVacunas ventana para alertas de vacunas (default 30)
     * @param diasAlertaInventario ventana para alertas de lotes (default 30)
     */
    public DashboardDTO obtenerDashboard(int diasPeriodo, int diasAlertaVacunas, int diasAlertaInventario) {
        log.info("Generando dashboard: periodo={}d, alertaVacunas={}d, alertaInventario={}d",
                diasPeriodo, diasAlertaVacunas, diasAlertaInventario);

        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setResumenDia(construirResumenDia());
        dashboard.setEstadisticasPeriodo(construirEstadisticasPeriodo(diasPeriodo));
        dashboard.setAlertas(construirAlertas(diasAlertaVacunas, diasAlertaInventario));
        return dashboard;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  RESUMEN DEL DÍA
    // ══════════════════════════════════════════════════════════════════════

    private ResumenDiaDTO construirResumenDia() {
        LocalDate hoy = LocalDate.now();
        ResumenDiaDTO dto = new ResumenDiaDTO();

        dto.setFecha(hoy.toString());
        dto.setTotalCitasHoy(citaRepository.countByFecha(hoy));
        dto.setCitasPendientes(citaRepository.countByFechaAndEstado(hoy, "pendiente"));
        dto.setCitasConfirmadas(citaRepository.countByFechaAndEstado(hoy, "confirmada"));
        dto.setCitasEnCurso(citaRepository.countByFechaAndEstado(hoy, "en_curso"));
        dto.setCitasCompletadas(citaRepository.countByFechaAndEstado(hoy, "completada"));
        dto.setCitasCanceladas(citaRepository.countByFechaAndEstado(hoy, "cancelada"));
        dto.setMascotasAtendidasHoy(citaRepository.countMascotasAtendidasEnFecha(hoy));

        return dto;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ESTADÍSTICAS DEL PERÍODO
    // ══════════════════════════════════════════════════════════════════════

    private EstadisticasPeriodoDTO construirEstadisticasPeriodo(int dias) {
        LocalDate fin    = LocalDate.now();
        LocalDate inicio = fin.minusDays(dias - 1);

        EstadisticasPeriodoDTO dto = new EstadisticasPeriodoDTO();
        dto.setFechaInicio(inicio.toString());
        dto.setFechaFin(fin.toString());
        dto.setDiasPeriodo(dias);

        // Citas
        long totalCitas      = citaRepository.countEnRango(inicio, fin);
        long completadas     = citaRepository.countEnRangoYEstado(inicio, fin, "completada");
        long canceladas      = citaRepository.countEnRangoYEstado(inicio, fin, "cancelada");
        double tasaComplet   = totalCitas > 0 ? Math.round((completadas * 100.0 / totalCitas) * 10.0) / 10.0 : 0;

        dto.setTotalCitas(totalCitas);
        dto.setCitasCompletadas(completadas);
        dto.setCitasCanceladas(canceladas);
        dto.setTasaCompletacion(tasaComplet);

        // Clínico
        dto.setTotalDiagnosticos(diagnosticoRepository.countByCitaFechaBetween(inicio, fin));

        // Rankings — top 5 en cada uno
        dto.setMedicosMasActivos(
                citaRepository.medicosConMasCitas(inicio, fin).stream()
                        .limit(5)
                        .map(r -> new ItemRankingDTO(
                                toInt(r[0]),
                                r[1] + " " + r[2],
                                toLong(r[3])))
                        .collect(Collectors.toList()));

        dto.setMascotasMasAtendidas(
                diagnosticoRepository.mascotasMasAtendidas(inicio, fin).stream()
                        .limit(5)
                        .map(r -> new ItemRankingDTO(
                                toInt(r[0]),
                                (String) r[1],
                                toLong(r[2])))
                        .collect(Collectors.toList()));

        dto.setCitasPorTipo(
                citaRepository.citasPorTipoEnRango(inicio, fin).stream()
                        .map(r -> new ItemRankingDTO(null, (String) r[0], toLong(r[1])))
                        .collect(Collectors.toList()));

        dto.setCitasPorEstado(
                citaRepository.citasPorEstadoEnRango(inicio, fin).stream()
                        .map(r -> new ItemRankingDTO(null, (String) r[0], toLong(r[1])))
                        .collect(Collectors.toList()));

        dto.setCitasPorDiaSemana(
                citaRepository.citasPorDiaSemana(inicio, fin).stream()
                        .map(r -> new ItemRankingDTO(null, ((String) r[0]).trim(), toLong(r[1])))
                        .collect(Collectors.toList()));

        // Inventario — productos más usados en tratamientos
        LocalDateTime inicioTs = inicio.atStartOfDay();
        LocalDateTime finTs    = fin.atTime(LocalTime.MAX);
        dto.setProductosMasUsados(
                movimientoRepository.productosMasUsados(inicioTs, finTs).stream()
                        .limit(10)
                        .map(r -> new ItemRankingDTO(
                                toInt(r[0]),
                                (String) r[1],
                                toLong(r[2])))
                        .collect(Collectors.toList()));

        return dto;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ALERTAS CONSOLIDADAS
    // ══════════════════════════════════════════════════════════════════════

    private AlertasDTO construirAlertas(int diasVacunas, int diasInventario) {
        AlertaVacunaDTO    alertaVac = vacunaService.obtenerAlertas(diasVacunas);
        AlertaInventarioDTO alertaInv = inventarioService.obtenerAlertas(diasInventario);

        AlertasDTO dto = new AlertasDTO();
        dto.setVacunasProximasAVencer(alertaVac.getTotalProximas());
        dto.setVacunasVencidas(alertaVac.getTotalVencidas());
        dto.setProductosConStockBajo(alertaInv.getTotalStockBajo());
        dto.setLotesProximosAVencer(alertaInv.getTotalLotesProximosAVencer());
        dto.setLotesVencidos(alertaInv.getTotalLotesVencidos());
        dto.setTotalAlertas(
                alertaVac.getTotalProximas() +
                alertaVac.getTotalVencidas() +
                alertaInv.getTotalStockBajo() +
                alertaInv.getTotalLotesProximosAVencer() +
                alertaInv.getTotalLotesVencidos());

        return dto;
    }

    // ── Helpers de casting seguro ──────────────────────────────────────────

    private Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Integer i) return i;
        if (o instanceof Long l)    return l.intValue();
        if (o instanceof Number n)  return n.intValue();
        return null;
    }

    private long toLong(Object o) {
        if (o == null) return 0L;
        if (o instanceof Long l)   return l;
        if (o instanceof Number n) return n.longValue();
        return 0L;
    }
}
