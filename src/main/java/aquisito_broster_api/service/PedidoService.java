package aquisito_broster_api.service;

import aquisito_broster_api.dto.request.EstadoPedidoRequest;
import aquisito_broster_api.dto.request.PedidoCreateRequest;
import aquisito_broster_api.dto.response.PedidoResponse;
import aquisito_broster_api.entity.EstadoPedido;

import java.util.List;

public interface PedidoService {

    PedidoResponse registerOrder(PedidoCreateRequest request, String username);

    List<PedidoResponse> listOrders(EstadoPedido estado);

    PedidoResponse updateOrderState(Long pedidoId, EstadoPedidoRequest request);
}