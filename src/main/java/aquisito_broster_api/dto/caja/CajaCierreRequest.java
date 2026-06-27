package aquisito_broster_api.dto.caja;

import jakarta.validation.constraints.NotBlank;

public class CajaCierreRequest {

    @NotBlank
    private String usuarioUsername;

    public String getUsuarioUsername() { return usuarioUsername; }
    public void setUsuarioUsername(String usuarioUsername) { this.usuarioUsername = usuarioUsername; }
}