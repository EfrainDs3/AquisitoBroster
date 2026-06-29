package aquisito_broster_api;

import aquisito_broster_api.dto.producto.ProductoRequest;
import aquisito_broster_api.dto.producto.ProductoResponse;
import aquisito_broster_api.entity.Producto;
import aquisito_broster_api.repository.ProductoRepository;
import aquisito_broster_api.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void listAllShouldReturnMappedProducts() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Café");
        producto.setDescripcion("Café premium");
        producto.setPrecio(new BigDecimal("12.50"));
        producto.setDisponible(true);
        producto.setCategoria("Bebidas");

        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<ProductoResponse> result = productoService.listAll();

        assertEquals(1, result.size());
        assertEquals("Café", result.get(0).getNombre());
        assertEquals(new BigDecimal("12.50"), result.get(0).getPrecio());
    }

    @Test
    void createShouldMapRequestToProduct() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Te");
        request.setDescripcion("Té verde");
        request.setPrecio(new BigDecimal("8.00"));
        request.setDisponible(true);
        request.setCategoria("Bebidas");

        Producto saved = new Producto();
        saved.setId(2L);
        saved.setNombre(request.getNombre());
        saved.setDescripcion(request.getDescripcion());
        saved.setPrecio(request.getPrecio());
        saved.setDisponible(request.isDisponible());
        saved.setCategoria(request.getCategoria());

        when(productoRepository.save(org.mockito.ArgumentMatchers.any(Producto.class))).thenReturn(saved);

        ProductoResponse response = productoService.crear(request);

        assertEquals("Te", response.getNombre());
        assertEquals("Bebidas", response.getCategoria());
    }
}
