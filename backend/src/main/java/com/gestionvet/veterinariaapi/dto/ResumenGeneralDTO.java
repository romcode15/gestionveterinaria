package com.gestionvet.veterinariaapi.dto;

/**
 * Conteos globales para las tarjetas del Dashboard de admin/recepcionista.
 * Todos los valores son COUNT de BD — nunca tamaños de página.
 */
public class ResumenGeneralDTO {

    private long totalClientesActivos;
    private long totalMascotasActivas;
    private long totalMedicosDisponibles;
    private long totalCitasHoy;

    public ResumenGeneralDTO() {}

    public ResumenGeneralDTO(long totalClientesActivos, long totalMascotasActivas,
                             long totalMedicosDisponibles, long totalCitasHoy) {
        this.totalClientesActivos    = totalClientesActivos;
        this.totalMascotasActivas    = totalMascotasActivas;
        this.totalMedicosDisponibles = totalMedicosDisponibles;
        this.totalCitasHoy           = totalCitasHoy;
    }

    public long getTotalClientesActivos()    { return totalClientesActivos; }
    public void setTotalClientesActivos(long v) { this.totalClientesActivos = v; }

    public long getTotalMascotasActivas()    { return totalMascotasActivas; }
    public void setTotalMascotasActivas(long v) { this.totalMascotasActivas = v; }

    public long getTotalMedicosDisponibles() { return totalMedicosDisponibles; }
    public void setTotalMedicosDisponibles(long v) { this.totalMedicosDisponibles = v; }

    public long getTotalCitasHoy()           { return totalCitasHoy; }
    public void setTotalCitasHoy(long v)     { this.totalCitasHoy = v; }
}
