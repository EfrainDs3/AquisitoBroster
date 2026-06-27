package aquisito_broster_api.dto.request;

import aquisito_broster_api.entity.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public class EstadoPedidoRequest {

    @NotNull
    private EstadoPedido estado;

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}