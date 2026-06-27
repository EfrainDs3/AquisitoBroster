package aquisito_broster_api.controller;

import aquisito_broster_api.dto.common.ApiResponse;
import aquisito_broster_api.dto.request.UsuarioRequest;
import aquisito_broster_api.dto.response.UsuarioResponse;
import aquisito_broster_api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ApiResponse<List<UsuarioResponse>> listar() {
        return new ApiResponse<>(true, usuarioService.listar(), "Usuarios obtenidos");
    }

    @PostMapping
    public ApiResponse<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        return new ApiResponse<>(true, usuarioService.crear(request), "Usuario creado");
    }

    @PostMapping("/{id}/activo")
    public ApiResponse<UsuarioResponse> cambiarActivo(@PathVariable Long id, @RequestParam boolean activo) {
        return new ApiResponse<>(true, usuarioService.cambiarActivo(id, activo), "Usuario actualizado");
    }
}