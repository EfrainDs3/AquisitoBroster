package aquisito_broster_api.controller;

import aquisito_broster_api.dto.common.ApiResponse;
import aquisito_broster_api.dto.caja.CajaAperturaRequest;
import aquisito_broster_api.dto.caja.CajaCierreRequest;
import aquisito_broster_api.dto.caja.CajaEgresoRequest;
import aquisito_broster_api.dto.caja.CajaResponse;
import aquisito_broster_api.service.CajaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/caja")
public class CajaController {

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    @GetMapping("/estado")
    public ApiResponse<CajaResponse> estado(@RequestBody CajaCierreRequest request) {
        return new ApiResponse<>(true, cajaService.estadoActual(request.getUsuarioUsername()), "Estado de caja");
    }

    @PostMapping("/apertura")
    public ApiResponse<CajaResponse> apertura(@Valid @RequestBody CajaAperturaRequest request) {
        return new ApiResponse<>(true, cajaService.abrir(request), "Caja abierta");
    }

    @PostMapping("/egreso")
    public ApiResponse<CajaResponse> egreso(@Valid @RequestBody CajaEgresoRequest request) {
        return new ApiResponse<>(true, cajaService.registrarEgreso(request), "Egreso registrado");
    }

    @PostMapping("/cierre")
    public ApiResponse<CajaResponse> cierre(@Valid @RequestBody CajaCierreRequest request) {
        return new ApiResponse<>(true, cajaService.cerrar(request), "Caja cerrada");
    }
}