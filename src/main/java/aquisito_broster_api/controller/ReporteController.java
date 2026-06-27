package aquisito_broster_api.controller;

import aquisito_broster_api.dto.common.ApiResponse;
import aquisito_broster_api.dto.response.ReporteResumenResponse;
import aquisito_broster_api.service.ReporteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/resumen-diario")
    public ApiResponse<ReporteResumenResponse> resumen() {
        return new ApiResponse<>(true, reporteService.getResumenDiario(), "Resumen generado");
    }
}