package com.gestionvet.veterinariaapi.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Respuesta del endpoint de alertas de inventario para el dashboard.
 */
public class AlertaInventarioDTO {

    private int totalStockBajo;
    private int totalLotesVencidos;
    private int totalLotesProximosAVencer;
    private int diasVentanaConsultados;

    private List<ItemStockBajoDTO>    stockBajo;
    private List<ItemLoteAlertaDTO>   lotesProximosAVencer;
    private List<ItemLoteAlertaDTO>   lotesVencidos;

    public AlertaInventarioDTO() {}

    public int getTotalStockBajo() { return totalStockBajo; }
    public void setTotalStockBajo(int totalStockBajo) { this.totalStockBajo = totalStockBajo; }

    public int getTotalLotesVencidos() { return totalLotesVencidos; }
    public void setTotalLotesVencidos(int totalLotesVencidos) { this.totalLotesVencidos = totalLotesVencidos; }

    public int getTotalLotesProximosAVencer() { return totalLotesProximosAVencer; }
    public void setTotalLotesProximosAVencer(int totalLotesProximosAVencer) { this.totalLotesProximosAVencer = totalLotesProximosAVencer; }

    public int getDiasVentanaConsultados() { return diasVentanaConsultados; }
    public void setDiasVentanaConsultados(int diasVentanaConsultados) { this.diasVentanaConsultados = diasVentanaConsultados; }

    public List<ItemStockBajoDTO> getStockBajo() { return stockBajo; }
    public void setStockBajo(List<ItemStockBajoDTO> stockBajo) { this.stockBajo = stockBajo; }

    public List<ItemLoteAlertaDTO> getLotesProximosAVencer() { return lotesProximosAVencer; }
    public void setLotesProximosAVencer(List<ItemLoteAlertaDTO> lotesProximosAVencer) { this.lotesProximosAVencer = lotesProximosAVencer; }

    public List<ItemLoteAlertaDTO> getLotesVencidos() { return lotesVencidos; }
    public void setLotesVencidos(List<ItemLoteAlertaDTO> lotesVencidos) { this.lotesVencidos = lotesVencidos; }

    // ── Producto con stock bajo ────────────────────────────────────────────
    public static class ItemStockBajoDTO {
        private Integer productoId;
        private String  productoNombre;
        private String  categoriaNombre;
        private String  unidadMedida;
        private Integer stockActual;
        private Integer stockMinimo;
        private Integer diferencia; // stockMinimo - stockActual

        public ItemStockBajoDTO() {}

        public Integer getProductoId() { return productoId; }
        public void setProductoId(Integer productoId) { this.productoId = productoId; }

        public String getProductoNombre() { return productoNombre; }
        public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

        public String getCategoriaNombre() { return categoriaNombre; }
        public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

        public String getUnidadMedida() { return unidadMedida; }
        public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

        public Integer getStockActual() { return stockActual; }
        public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

        public Integer getStockMinimo() { return stockMinimo; }
        public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

        public Integer getDiferencia() { return diferencia; }
        public void setDiferencia(Integer diferencia) { this.diferencia = diferencia; }
    }

    // ── Lote próximo a vencer o ya vencido ────────────────────────────────
    public static class ItemLoteAlertaDTO {
        private Integer   loteId;
        private String    numeroLote;
        private Integer   productoId;
        private String    productoNombre;
        private String    categoriaNombre;
        private Integer   cantidadActual;
        private String    unidadMedida;
        private LocalDate fechaVencimiento;
        private Long      diasRestantes; // negativo = ya venció

        public ItemLoteAlertaDTO() {}

        public Integer getLoteId() { return loteId; }
        public void setLoteId(Integer loteId) { this.loteId = loteId; }

        public String getNumeroLote() { return numeroLote; }
        public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

        public Integer getProductoId() { return productoId; }
        public void setProductoId(Integer productoId) { this.productoId = productoId; }

        public String getProductoNombre() { return productoNombre; }
        public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

        public String getCategoriaNombre() { return categoriaNombre; }
        public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

        public Integer getCantidadActual() { return cantidadActual; }
        public void setCantidadActual(Integer cantidadActual) { this.cantidadActual = cantidadActual; }

        public String getUnidadMedida() { return unidadMedida; }
        public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

        public LocalDate getFechaVencimiento() { return fechaVencimiento; }
        public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

        public Long getDiasRestantes() { return diasRestantes; }
        public void setDiasRestantes(Long diasRestantes) { this.diasRestantes = diasRestantes; }
    }
}
