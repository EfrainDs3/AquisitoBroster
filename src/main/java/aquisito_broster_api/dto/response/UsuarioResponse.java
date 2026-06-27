package aquisito_broster_api.dto.response;

public class UsuarioResponse {

    private Long id;
    private String username;
    private String nombre;
    private String rol;

    public UsuarioResponse(Long id, String username, String nombre, String rol) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.rol = rol;
    }

    public Long getId() {
        return id;
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