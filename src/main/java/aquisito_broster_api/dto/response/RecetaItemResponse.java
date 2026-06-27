package aquisito_broster_api.dto.response;

import java.math.BigDecimal;

public class RecetaItemResponse {

    private Long insumoId;
    private String insumoNombre;
    private BigDecimal cantidadRequerida;
    private String unidadMedida;

    public RecetaItemResponse(Long insumoId, String insumoNombre, BigDecimal cantidadRequerida, String unidadMedida) {
        this.insumoId = insumoId;
        this.insumoNombre = insumoNombre;
        this.cantidadRequerida = cantidadRequerida;
        this.unidadMedida = unidadMedida;
    }

    public Long getInsumoId() { return insumoId; }
    public String getInsumoNombre() { return insumoNombre; }
    public BigDecimal getCantidadRequerida() { return cantidadRequerida; }
    public String getUnidadMedida() { return unidadMedida; }
}