package aquisito_broster_api.dto.caja;

import aquisito_broster_api.entity.EstadoCaja;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CajaResponse {

    private Long id;
    private String usuario;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private BigDecimal montoApertura;
    private BigDecimal ingresosVentas;
    private BigDecimal egresosAdicionales;
    private BigDecimal montoCierre;
    private EstadoCaja estado;

    public CajaResponse(Long id, String usuario, LocalDateTime fechaApertura, LocalDateTime fechaCierre, BigDecimal montoApertura, BigDecimal ingresosVentas, BigDecimal egresosAdicionales, BigDecimal montoCierre, EstadoCaja estado) {
        this.id = id;
        this.usuario = usuario;
        this.fechaApertura = fechaApertura;
        this.fechaCierre = fechaCierre;
        this.montoApertura = montoApertura;
        this.ingresosVentas = ingresosVentas;
        this.egresosAdicionales = egresosAdicionales;
        this.montoCierre = montoCierre;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public String getUsuario() { return usuario; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public BigDecimal getMontoApertura() { return montoApertura; }
    public BigDecimal getIngresosVentas() { return ingresosVentas; }
    public BigDecimal getEgresosAdicionales() { return egresosAdicionales; }
    public BigDecimal getMontoCierre() { return montoCierre; }
    public EstadoCaja getEstado() { return estado; }
}