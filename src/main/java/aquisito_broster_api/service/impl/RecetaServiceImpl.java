package aquisito_broster_api.service.impl;

import aquisito_broster_api.dto.request.RecetaItemRequest;
import aquisito_broster_api.dto.response.RecetaItemResponse;
import aquisito_broster_api.entity.Receta;
import aquisito_broster_api.exception.ResourceNotFoundException;
import aquisito_broster_api.repository.InsumoRepository;
import aquisito_broster_api.repository.ProductoRepository;
import aquisito_broster_api.repository.RecetaRepository;
import aquisito_broster_api.service.RecetaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final ProductoRepository productoRepository;
    private final InsumoRepository insumoRepository;

    public RecetaServiceImpl(RecetaRepository recetaRepository, ProductoRepository productoRepository, InsumoRepository insumoRepository) {
        this.recetaRepository = recetaRepository;
        this.productoRepository = productoRepository;
        this.insumoRepository = insumoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecetaItemResponse> listarPorProducto(Long productoId) {
        return recetaRepository.findByProductoId(productoId).stream().map(this::toResponse).toList();
    }

    @Override
    public RecetaItemResponse crear(RecetaItemRequest request) {
        var producto = productoRepository.findById(request.getProductoId())
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        var insumo = insumoRepository.findById(request.getInsumoId())
            .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado"));

        Receta receta = new Receta();
        receta.setProducto(producto);
        receta.setInsumo(insumo);
        receta.setCantidadRequerida(request.getCantidadRequerida());
        Receta saved = recetaRepository.save(receta);
        return toResponse(saved);
    }

    @Override
    public void eliminar(Long id) {
        if (!recetaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Receta no encontrada");
        }
        recetaRepository.deleteById(id);
    }

    private RecetaItemResponse toResponse(Receta receta) {
        return new RecetaItemResponse(
            receta.getInsumo().getId(),
            receta.getInsumo().getNombre(),
            receta.getCantidadRequerida(),
            receta.getInsumo().getUnidadMedida().name());
    }
}
