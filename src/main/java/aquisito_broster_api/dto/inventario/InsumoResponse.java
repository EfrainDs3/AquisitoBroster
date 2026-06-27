package aquisito_broster_api.dto.inventario;

import java.math.BigDecimal;

public class InsumoResponse {

    private Long id;
    private String nombre;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private String unidadMedida;
    private boolean stockCritico;

    public InsumoResponse(Long id, String nombre, BigDecimal stockActual, BigDecimal stockMinimo, String unidadMedida, boolean stockCritico) {
        this.id = id;
        this.nombre = nombre;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.unidadMedida = unidadMedida;
        this.stockCritico = stockCritico;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getStockActual() { return stockActual; }
    public BigDecimal getStockMinimo() { return stockMinimo; }
    public String getUnidadMedida() { return unidadMedida; }
    public boolean isStockCritico() { return stockCritico; }
}