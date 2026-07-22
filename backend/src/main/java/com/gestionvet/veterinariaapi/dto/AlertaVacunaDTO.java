package com.gestionvet.veterinariaapi.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Respuesta del endpoint de alertas de vacunación para el dashboard.
 */
public class AlertaVacunaDTO {

    private int totalProximas;   // vencen en los próximos N días
    private int totalVencidas;   // ya vencidas sin renovar
    private int diasConsultados; // ventana de búsqueda usada

    private List<ItemAlertaDTO> proximas;
    private List<ItemAlertaDTO> vencidas;

    public AlertaVacunaDTO() {}

    public int getTotalProximas() { return totalProximas; }
    public void setTotalProximas(int totalProximas) { this.totalProximas = totalProximas; }

    public int getTotalVencidas() { return totalVencidas; }
    public void setTotalVencidas(int totalVencidas) { this.totalVencidas = totalVencidas; }

    public int getDiasConsultados() { return diasConsultados; }
    public void setDiasConsultados(int diasConsultados) { this.diasConsultados = diasConsultados; }

    public List<ItemAlertaDTO> getProximas() { return proximas; }
    public void setProximas(List<ItemAlertaDTO> proximas) { this.proximas = proximas; }

    public List<ItemAlertaDTO> getVencidas() { return vencidas; }
    public void setVencidas(List<ItemAlertaDTO> vencidas) { this.vencidas = vencidas; }

    // ── Clase interna: una alerta individual ──────────────────────────────
    public static class ItemAlertaDTO {

        private Integer   mascotaVacunaId;
        private Integer   mascotaId;
        private String    mascotaNombre;
        private String    especieNombre;
        private String    clienteNombre;
        private String    clienteTelefono;
        private String    vacunaNombre;
        private LocalDate fechaAplicacion;
        private LocalDate fechaProximaDosis;
        private Long      diasRestantes;   // negativo si ya venció

        public ItemAlertaDTO() {}

        public Integer getMascotaVacunaId() { return mascotaVacunaId; }
        public void setMascotaVacunaId(Integer mascotaVacunaId) { this.mascotaVacunaId = mascotaVacunaId; }

        public Integer getMascotaId() { return mascotaId; }
        public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }

        public String getMascotaNombre() { return mascotaNombre; }
        public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }

        public String getEspecieNombre() { return especieNombre; }
        public void setEspecieNombre(String especieNombre) { this.especieNombre = especieNombre; }

        public String getClienteNombre() { return clienteNombre; }
        public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

        public String getClienteTelefono() { return clienteTelefono; }
        public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }

        public String getVacunaNombre() { return vacunaNombre; }
        public void setVacunaNombre(String vacunaNombre) { this.vacunaNombre = vacunaNombre; }

        public LocalDate getFechaAplicacion() { return fechaAplicacion; }
        public void setFechaAplicacion(LocalDate fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; }

        public LocalDate getFechaProximaDosis() { return fechaProximaDosis; }
        public void setFechaProximaDosis(LocalDate fechaProximaDosis) { this.fechaProximaDosis = fechaProximaDosis; }

        public Long getDiasRestantes() { return diasRestantes; }
        public void setDiasRestantes(Long diasRestantes) { this.diasRestantes = diasRestantes; }
    }
}
