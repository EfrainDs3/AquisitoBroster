package aquisito_broster_api.controller;

import aquisito_broster_api.dto.common.ApiResponse;
import aquisito_broster_api.dto.producto.ProductoRequest;
import aquisito_broster_api.dto.producto.ProductoResponse;
import aquisito_broster_api.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ApiResponse<List<ProductoResponse>> listar() {
        return new ApiResponse<>(true, productoService.listAll(), "Productos obtenidos");
    }

    @PostMapping
    public ApiResponse<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return new ApiResponse<>(true, productoService.crear(request), "Producto creado");
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return new ApiResponse<>(true, productoService.actualizar(id, request), "Producto actualizado");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return new ApiResponse<>(true, null, "Producto eliminado");
    }
}