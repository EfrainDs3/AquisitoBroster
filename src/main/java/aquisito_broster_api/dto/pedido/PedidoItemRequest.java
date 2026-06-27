package aquisito_broster_api.dto.pedido;

import jakarta.validation.constraints.NotNull;

public class PedidoItemRequest {

    @NotNull
    private Long productoId;

    @NotNull
    private Integer cantidad;

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}