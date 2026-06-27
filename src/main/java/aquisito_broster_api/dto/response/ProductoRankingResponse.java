package aquisito_broster_api.dto.response;

import java.math.BigDecimal;

public class ProductoRankingResponse {

    private String nombre;
    private long vendidos;
    private BigDecimal recaudado;

    public ProductoRankingResponse(String nombre, long vendidos, BigDecimal recaudado) {
        this.nombre = nombre;
        this.vendidos = vendidos;
        this.recaudado = recaudado;
    }

    public String getNombre() { return nombre; }
    public long getVendidos() { return vendidos; }
    public BigDecimal getRecaudado() { return recaudado; }
}