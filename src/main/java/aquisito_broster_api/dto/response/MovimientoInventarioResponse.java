package aquisito_broster_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoInventarioResponse {

    private Long id;
    private String insumo;
    private String tipo;
    private BigDecimal cantidad;
    private String motivo;
    private LocalDateTime fecha;
    private String responsable;

    public MovimientoInventarioResponse(Long id, String insumo, String tipo, BigDecimal cantidad, String motivo, LocalDateTime fecha, String responsable) {
        this.id = id;
        this.insumo = insumo;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = fecha;
        this.responsable = responsable;
    }

    public Long getId() { return id; }
    public String getInsumo() { return insumo; }
    public String getTipo() { return tipo; }
    public BigDecimal getCantidad() { return cantidad; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getFecha() { return fecha; }
    public String getResponsable() { return responsable; }
}