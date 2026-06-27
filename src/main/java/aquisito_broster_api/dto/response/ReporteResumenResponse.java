package aquisito_broster_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ReporteResumenResponse {

    private BigDecimal ventasTotales;
    private BigDecimal ticketPromedio;
    private long pedidosRegistrados;
    private long mermasRegistradas;
    private long insumosCriticos;
    private List<ProductoRankingResponse> rankingProductos;

    public ReporteResumenResponse(BigDecimal ventasTotales, BigDecimal ticketPromedio, long pedidosRegistrados, long mermasRegistradas, long insumosCriticos, List<ProductoRankingResponse> rankingProductos) {
        this.ventasTotales = ventasTotales;
        this.ticketPromedio = ticketPromedio;
        this.pedidosRegistrados = pedidosRegistrados;
        this.mermasRegistradas = mermasRegistradas;
        this.insumosCriticos = insumosCriticos;
        this.rankingProductos = rankingProductos;
    }

    public BigDecimal getVentasTotales() { return ventasTotales; }
    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public long getPedidosRegistrados() { return pedidosRegistrados; }
    public long getMermasRegistradas() { return mermasRegistradas; }
    public long getInsumosCriticos() { return insumosCriticos; }
    public List<ProductoRankingResponse> getRankingProductos() { return rankingProductos; }
}