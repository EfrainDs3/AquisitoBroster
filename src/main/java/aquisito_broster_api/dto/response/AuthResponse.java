package aquisito_broster_api.dto.response;

public class AuthResponse {

    private String token;
    private String username;
    private String nombre;
    private String rol;

    public AuthResponse(String token, String username, String nombre, String rol) {
        this.token = token;
        this.username = username;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }
}