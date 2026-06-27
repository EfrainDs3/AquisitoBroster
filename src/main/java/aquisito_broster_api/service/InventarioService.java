package aquisito_broster_api.service;

import aquisito_broster_api.dto.request.InsumoMovimientoRequest;
import aquisito_broster_api.dto.response.InsumoResponse;
import aquisito_broster_api.dto.response.MovimientoInventarioResponse;
import aquisito_broster_api.dto.response.RecetaItemResponse;

import java.util.List;

public interface InventarioService {

    List<InsumoResponse> listInsumos();

    List<MovimientoInventarioResponse> listMovimientos();

    MovimientoInventarioResponse registerMovement(InsumoMovimientoRequest request, String username);

    List<RecetaItemResponse> listRecetaByProducto(Long productoId);
}