package aquisito_broster_api.service;

import aquisito_broster_api.dto.request.RecetaItemRequest;
import aquisito_broster_api.dto.response.RecetaItemResponse;

import java.util.List;

public interface RecetaService {

    List<RecetaItemResponse> listarPorProducto(Long productoId);

    RecetaItemResponse crear(RecetaItemRequest request);

    void eliminar(Long id);
}