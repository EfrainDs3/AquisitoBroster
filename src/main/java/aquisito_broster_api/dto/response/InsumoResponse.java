package aquisito_broster_api.dto.response;

import java.math.BigDecimal;

public class InsumoResponse {

    private Long id;
    private String nombre;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private String unidadMedida;
    private boolean lowStock;

    public InsumoResponse(Long id, String nombre, BigDecimal stockActual, BigDecimal stockMinimo, String unidadMedida, boolean lowStock) {
        this.id = id;
        this.nombre = nombre;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.unidadMedida = unidadMedida;
        this.lowStock = lowStock;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getStockActual() { return stockActual; }
    public BigDecimal getStockMinimo() { return stockMinimo; }
    public String getUnidadMedida() { return unidadMedida; }
    public boolean isLowStock() { return lowStock; }
}