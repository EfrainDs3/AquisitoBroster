package aquisito_broster_api.dto.pedido;

import aquisito_broster_api.entity.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {

    private Long id;
    private LocalDateTime fecha;
    private String cliente;
    private EstadoPedido estado;
    private BigDecimal total;
    private String usuario;
    private List<PedidoItemResponse> items;

    public PedidoResponse(Long id, LocalDateTime fecha, String cliente, EstadoPedido estado, BigDecimal total, String usuario, List<PedidoItemResponse> items) {
        this.id = id;
        this.fecha = fecha;
        this.cliente = cliente;
        this.estado = estado;
        this.total = total;
        this.usuario = usuario;
        this.items = items;
    }

    public Long getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public String getCliente() { return cliente; }
    public EstadoPedido getEstado() { return estado; }
    public BigDecimal getTotal() { return total; }
    public String getUsuario() { return usuario; }
    public List<PedidoItemResponse> getItems() { return items; }
}