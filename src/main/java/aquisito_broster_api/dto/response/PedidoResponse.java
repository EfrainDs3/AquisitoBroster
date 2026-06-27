package aquisito_broster_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {

    private Long id;
    private LocalDateTime fecha;
    private String cliente;
    private String estado;
    private BigDecimal total;
    private List<DetallePedidoResponse> items;

    public PedidoResponse(Long id, LocalDateTime fecha, String cliente, String estado, BigDecimal total, List<DetallePedidoResponse> items) {
        this.id = id;
        this.fecha = fecha;
        this.cliente = cliente;
        this.estado = estado;
        this.total = total;
        this.items = items;
    }

    public Long getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public String getCliente() { return cliente; }
    public String getEstado() { return estado; }
    public BigDecimal getTotal() { return total; }
    public List<DetallePedidoResponse> getItems() { return items; }
}