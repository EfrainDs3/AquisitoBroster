package aquisito_broster_api.dto.reporte;

import java.math.BigDecimal;
import java.util.List;

public class ReporteResumenResponse {

    private BigDecimal ventasTotales;
    private long pedidosRegistrados;
    private long mermasRegistradas;
    private BigDecimal ticketPromedio;
    private long stockCritico;
    private List<TopProducto> topProductos;

    public ReporteResumenResponse(BigDecimal ventasTotales, long pedidosRegistrados, long mermasRegistradas, BigDecimal ticketPromedio, long stockCritico, List<TopProducto> topProductos) {
        this.ventasTotales = ventasTotales;
        this.pedidosRegistrados = pedidosRegistrados;
        this.mermasRegistradas = mermasRegistradas;
        this.ticketPromedio = ticketPromedio;
        this.stockCritico = stockCritico;
        this.topProductos = topProductos;
    }

    public BigDecimal getVentasTotales() { return ventasTotales; }
    public long getPedidosRegistrados() { return pedidosRegistrados; }
    public long getMermasRegistradas() { return mermasRegistradas; }
    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public long getStockCritico() { return stockCritico; }
    public List<TopProducto> getTopProductos() { return topProductos; }

    public static class TopProducto {
        private String nombre;
        private long vendidos;
        private BigDecimal recaudado;

        public TopProducto(String nombre, long vendidos, BigDecimal recaudado) {
            this.nombre = nombre;
            this.vendidos = vendidos;
            this.recaudado = recaudado;
        }

        public String getNombre() { return nombre; }
        public long getVendidos() { return vendidos; }
        public BigDecimal getRecaudado() { return recaudado; }
    }
}