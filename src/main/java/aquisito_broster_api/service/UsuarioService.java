package aquisito_broster_api.service;

import aquisito_broster_api.dto.request.UsuarioRequest;
import aquisito_broster_api.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> listar();

    UsuarioResponse crear(UsuarioRequest request);

    UsuarioResponse cambiarActivo(Long id, boolean activo);
}