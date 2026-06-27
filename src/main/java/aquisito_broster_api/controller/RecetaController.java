package aquisito_broster_api.controller;

import aquisito_broster_api.dto.common.ApiResponse;
import aquisito_broster_api.dto.request.RecetaItemRequest;
import aquisito_broster_api.dto.response.RecetaItemResponse;
import aquisito_broster_api.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @GetMapping
    public ApiResponse<List<RecetaItemResponse>> listar(@RequestParam Long productoId) {
        return new ApiResponse<>(true, recetaService.listarPorProducto(productoId), "Recetas obtenidas");
    }

    @PostMapping
    public ApiResponse<RecetaItemResponse> crear(@Valid @RequestBody RecetaItemRequest request) {
        return new ApiResponse<>(true, recetaService.crear(request), "Receta creada");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> eliminar(@PathVariable Long id) {
        recetaService.eliminar(id);
        return new ApiResponse<>(true, null, "Receta eliminada");
    }
}