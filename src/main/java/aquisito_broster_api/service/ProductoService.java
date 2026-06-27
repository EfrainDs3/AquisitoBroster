package aquisito_broster_api.service;

import aquisito_broster_api.dto.producto.ProductoRequest;
import aquisito_broster_api.dto.producto.ProductoResponse;

import java.util.List;

public interface ProductoService {

    List<ProductoResponse> listar();

    ProductoResponse crear(ProductoRequest request);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    void eliminar(Long id);
}