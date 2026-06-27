package aquisito_broster_api.controller;

import aquisito_broster_api.dto.common.ApiResponse;
import aquisito_broster_api.dto.request.EstadoPedidoRequest;
import aquisito_broster_api.dto.request.PedidoCreateRequest;
import aquisito_broster_api.dto.response.PedidoResponse;
import aquisito_broster_api.entity.EstadoPedido;
import aquisito_broster_api.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ApiResponse<PedidoResponse> registrar(@Valid @RequestBody PedidoCreateRequest request, Authentication authentication) {
        return new ApiResponse<>(true, pedidoService.registerOrder(request, authentication.getName()), "Pedido registrado");
    }

    @GetMapping
    public ApiResponse<List<PedidoResponse>> listar(@RequestParam(required = false) EstadoPedido estado) {
        return new ApiResponse<>(true, pedidoService.listOrders(estado), "Pedidos obtenidos");
    }

    @PatchMapping("/{id}/estado")
    public ApiResponse<PedidoResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoPedidoRequest request) {
        return new ApiResponse<>(true, pedidoService.updateOrderState(id, request), "Estado actualizado");
    }
}