package aquisito_broster_api.service.impl;

import aquisito_broster_api.dto.producto.ProductoRequest;
import aquisito_broster_api.dto.producto.ProductoResponse;
import aquisito_broster_api.entity.Producto;
import aquisito_broster_api.exception.ResourceNotFoundException;
import aquisito_broster_api.repository.ProductoRepository;
import aquisito_broster_api.service.ProductoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto();
        apply(producto, request);
        return toResponse(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        apply(producto, request);
        return toResponse(productoRepository.save(producto));
    }

    @Override
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    private void apply(Producto producto, ProductoRequest request) {
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setDisponible(request.isDisponible());
        producto.setCategoria(request.getCategoria());
    }

    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(producto.getId(), producto.getNombre(), producto.getDescripcion(), producto.getPrecio(), producto.isDisponible(), producto.getCategoria());
    }
}