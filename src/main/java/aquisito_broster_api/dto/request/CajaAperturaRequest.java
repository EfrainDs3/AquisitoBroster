package aquisito_broster_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CajaAperturaRequest {

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal montoApertura;

    public BigDecimal getMontoApertura() {
        return montoApertura;
    }

    public void setMontoApertura(BigDecimal montoApertura) {
        this.montoApertura = montoApertura;
    }
}