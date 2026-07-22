package com.gestionvet.veterinariaapi.dto;

import java.util.List;

/**
 * Respuesta completa del dashboard principal.
 * Agrupa resumen del día, período configurable y alertas críticas.
 */
public class DashboardDTO {

    // ── Resumen del día de hoy ─────────────────────────────────────────────
    private ResumenDiaDTO resumenDia;

    // ── Estadísticas del período (últimos 30 días por defecto) ────────────
    private EstadisticasPeriodoDTO estadisticasPeriodo;

    // ── Alertas críticas (consolidadas de vacunas + inventario) ───────────
    private AlertasDTO alertas;

    public DashboardDTO() {}

    public ResumenDiaDTO getResumenDia() { return resumenDia; }
    public void setResumenDia(ResumenDiaDTO resumenDia) { this.resumenDia = resumenDia; }

    public EstadisticasPeriodoDTO getEstadisticasPeriodo() { return estadisticasPeriodo; }
    public void setEstadisticasPeriodo(EstadisticasPeriodoDTO estadisticasPeriodo) { this.estadisticasPeriodo = estadisticasPeriodo; }

    public AlertasDTO getAlertas() { return alertas; }
    public void setAlertas(AlertasDTO alertas) { this.alertas = alertas; }

    // ══════════════════════════════════════════════════════════════════════
    //  RESUMEN DEL DÍA
    // ══════════════════════════════════════════════════════════════════════
    public static class ResumenDiaDTO {
        private String  fecha;
        private long    totalCitasHoy;
        private long    citasPendientes;
        private long    citasConfirmadas;
        private long    citasEnCurso;
        private long    citasCompletadas;
        private long    citasCanceladas;
        private long    mascotasAtendidasHoy;

        public ResumenDiaDTO() {}

        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }

        public long getTotalCitasHoy() { return totalCitasHoy; }
        public void setTotalCitasHoy(long totalCitasHoy) { this.totalCitasHoy = totalCitasHoy; }

        public long getCitasPendientes() { return citasPendientes; }
        public void setCitasPendientes(long citasPendientes) { this.citasPendientes = citasPendientes; }

        public long getCitasConfirmadas() { return citasConfirmadas; }
        public void setCitasConfirmadas(long citasConfirmadas) { this.citasConfirmadas = citasConfirmadas; }

        public long getCitasEnCurso() { return citasEnCurso; }
        public void setCitasEnCurso(long citasEnCurso) { this.citasEnCurso = citasEnCurso; }

        public long getCitasCompletadas() { return citasCompletadas; }
        public void setCitasCompletadas(long citasCompletadas) { this.citasCompletadas = citasCompletadas; }

        public long getCitasCanceladas() { return citasCanceladas; }
        public void setCitasCanceladas(long citasCanceladas) { this.citasCanceladas = citasCanceladas; }

        public long getMascotasAtendidasHoy() { return mascotasAtendidasHoy; }
        public void setMascotasAtendidasHoy(long mascotasAtendidasHoy) { this.mascotasAtendidasHoy = mascotasAtendidasHoy; }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ESTADÍSTICAS DEL PERÍODO
    // ══════════════════════════════════════════════════════════════════════
    public static class EstadisticasPeriodoDTO {
        private String fechaInicio;
        private String fechaFin;
        private int    diasPeriodo;

        // Citas
        private long   totalCitas;
        private long   citasCompletadas;
        private long   citasCanceladas;
        private double tasaCompletacion; // % de citas completadas

        // Clínico
        private long   totalDiagnosticos;

        // Rankings
        private List<ItemRankingDTO> medicosMasActivos;
        private List<ItemRankingDTO> mascotasMasAtendidas;
        private List<ItemRankingDTO> citasPorTipo;
        private List<ItemRankingDTO> citasPorEstado;
        private List<ItemRankingDTO> citasPorDiaSemana;

        // Inventario
        private List<ItemRankingDTO> productosMasUsados;

        public EstadisticasPeriodoDTO() {}

        public String getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

        public String getFechaFin() { return fechaFin; }
        public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

        public int getDiasPeriodo() { return diasPeriodo; }
        public void setDiasPeriodo(int diasPeriodo) { this.diasPeriodo = diasPeriodo; }

        public long getTotalCitas() { return totalCitas; }
        public void setTotalCitas(long totalCitas) { this.totalCitas = totalCitas; }

        public long getCitasCompletadas() { return citasCompletadas; }
        public void setCitasCompletadas(long citasCompletadas) { this.citasCompletadas = citasCompletadas; }

        public long getCitasCanceladas() { return citasCanceladas; }
        public void setCitasCanceladas(long citasCanceladas) { this.citasCanceladas = citasCanceladas; }

        public double getTasaCompletacion() { return tasaCompletacion; }
        public void setTasaCompletacion(double tasaCompletacion) { this.tasaCompletacion = tasaCompletacion; }

        public long getTotalDiagnosticos() { return totalDiagnosticos; }
        public void setTotalDiagnosticos(long totalDiagnosticos) { this.totalDiagnosticos = totalDiagnosticos; }

        public List<ItemRankingDTO> getMedicosMasActivos() { return medicosMasActivos; }
        public void setMedicosMasActivos(List<ItemRankingDTO> medicosMasActivos) { this.medicosMasActivos = medicosMasActivos; }

        public List<ItemRankingDTO> getMascotasMasAtendidas() { return mascotasMasAtendidas; }
        public void setMascotasMasAtendidas(List<ItemRankingDTO> mascotasMasAtendidas) { this.mascotasMasAtendidas = mascotasMasAtendidas; }

        public List<ItemRankingDTO> getCitasPorTipo() { return citasPorTipo; }
        public void setCitasPorTipo(List<ItemRankingDTO> citasPorTipo) { this.citasPorTipo = citasPorTipo; }

        public List<ItemRankingDTO> getCitasPorEstado() { return citasPorEstado; }
        public void setCitasPorEstado(List<ItemRankingDTO> citasPorEstado) { this.citasPorEstado = citasPorEstado; }

        public List<ItemRankingDTO> getCitasPorDiaSemana() { return citasPorDiaSemana; }
        public void setCitasPorDiaSemana(List<ItemRankingDTO> citasPorDiaSemana) { this.citasPorDiaSemana = citasPorDiaSemana; }

        public List<ItemRankingDTO> getProductosMasUsados() { return productosMasUsados; }
        public void setProductosMasUsados(List<ItemRankingDTO> productosMasUsados) { this.productosMasUsados = productosMasUsados; }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ALERTAS CRÍTICAS CONSOLIDADAS
    // ══════════════════════════════════════════════════════════════════════
    public static class AlertasDTO {
        private int totalAlertas;

        // Vacunación
        private int vacunasProximasAVencer;
        private int vacunasVencidas;

        // Inventario
        private int productosConStockBajo;
        private int lotesProximosAVencer;
        private int lotesVencidos;

        public AlertasDTO() {}

        public int getTotalAlertas() { return totalAlertas; }
        public void setTotalAlertas(int totalAlertas) { this.totalAlertas = totalAlertas; }

        public int getVacunasProximasAVencer() { return vacunasProximasAVencer; }
        public void setVacunasProximasAVencer(int vacunasProximasAVencer) { this.vacunasProximasAVencer = vacunasProximasAVencer; }

        public int getVacunasVencidas() { return vacunasVencidas; }
        public void setVacunasVencidas(int vacunasVencidas) { this.vacunasVencidas = vacunasVencidas; }

        public int getProductosConStockBajo() { return productosConStockBajo; }
        public void setProductosConStockBajo(int productosConStockBajo) { this.productosConStockBajo = productosConStockBajo; }

        public int getLotesProximosAVencer() { return lotesProximosAVencer; }
        public void setLotesProximosAVencer(int lotesProximosAVencer) { this.lotesProximosAVencer = lotesProximosAVencer; }

        public int getLotesVencidos() { return lotesVencidos; }
        public void setLotesVencidos(int lotesVencidos) { this.lotesVencidos = lotesVencidos; }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ITEM GENÉRICO DE RANKING
    // ══════════════════════════════════════════════════════════════════════
    public static class ItemRankingDTO {
        private Integer id;
        private String  etiqueta;
        private long    total;

        public ItemRankingDTO() {}
        public ItemRankingDTO(Integer id, String etiqueta, long total) {
            this.id       = id;
            this.etiqueta = etiqueta;
            this.total    = total;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getEtiqueta() { return etiqueta; }
        public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
    }
}
