package aquisito_broster_api.controller;

import aquisito_broster_api.dto.common.ApiResponse;
import aquisito_broster_api.dto.inventario.InsumoResponse;
import aquisito_broster_api.dto.inventario.MovimientoInventarioRequest;
import aquisito_broster_api.dto.response.MovimientoInventarioResponse;
import aquisito_broster_api.dto.response.RecetaItemResponse;
import aquisito_broster_api.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/insumos")
    public ApiResponse<List<InsumoResponse>> listarInsumos() {
        return new ApiResponse<>(true, inventarioService.listInsumos(), "Insumos obtenidos");
    }

    @GetMapping("/recetas")
    public ApiResponse<List<RecetaItemResponse>> listarRecetas(@RequestParam Long productoId) {
        return new ApiResponse<>(true, inventarioService.listRecetaByProducto(productoId), "Recetas obtenidas");
    }

    @GetMapping("/movimientos")
    public ApiResponse<List<MovimientoInventarioResponse>> listarMovimientos() {
        return new ApiResponse<>(true, inventarioService.listMovimientos(), "Movimientos obtenidos");
    }

    @PostMapping("/movimientos")
    public ApiResponse<MovimientoInventarioResponse> registrarMovimiento(@Valid @RequestBody MovimientoInventarioRequest request, Authentication authentication) {
        return new ApiResponse<>(true, inventarioService.registerMovement(request, authentication.getName()), "Movimiento registrado");
    }
}