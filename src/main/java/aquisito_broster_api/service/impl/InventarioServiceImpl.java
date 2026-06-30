package aquisito_broster_api.service.impl;

import aquisito_broster_api.dto.request.InsumoMovimientoRequest;
import aquisito_broster_api.dto.response.InsumoResponse;
import aquisito_broster_api.dto.response.MovimientoInventarioResponse;
import aquisito_broster_api.dto.response.RecetaItemResponse;
import aquisito_broster_api.entity.Insumo;
import aquisito_broster_api.entity.MovimientoInventario;
import aquisito_broster_api.entity.Receta;
import aquisito_broster_api.entity.TipoMovimiento;
import aquisito_broster_api.exception.ResourceNotFoundException;
import aquisito_broster_api.repository.InsumoRepository;
import aquisito_broster_api.repository.MovimientoInventarioRepository;
import aquisito_broster_api.repository.RecetaRepository;
import aquisito_broster_api.repository.UsuarioRepository;
import aquisito_broster_api.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final RecetaRepository recetaRepository;
    private final UsuarioRepository usuarioRepository;

    public InventarioServiceImpl(InsumoRepository insumoRepository, MovimientoInventarioRepository movimientoRepository, RecetaRepository recetaRepository, UsuarioRepository usuarioRepository) {
        this.insumoRepository = insumoRepository;
        this.movimientoRepository = movimientoRepository;
        this.recetaRepository = recetaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<InsumoResponse> listInsumos() {
        return insumoRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public List<MovimientoInventarioResponse> listMovimientos() {
        LocalDateTime now = LocalDateTime.now();
        return movimientoRepository.findByFechaBetween(now.toLocalDate().atStartOfDay(), now)
            .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public MovimientoInventarioResponse registerMovement(InsumoMovimientoRequest request, String username) {
        Insumo insumo = insumoRepository.findById(request.getInsumoId())
            .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado"));

        var usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        BigDecimal newStock = request.getTipo() == TipoMovimiento.ENTRADA
            ? insumo.getStockActual().add(request.getCantidad())
            : insumo.getStockActual().subtract(request.getCantidad());

        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResourceNotFoundException("Stock insuficiente para registrar el movimiento");
        }

        insumo.setStockActual(newStock);
        insumoRepository.save(insumo);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setInsumo(insumo);
        movimiento.setTipo(request.getTipo());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setMotivo(request.getMotivo());
        movimiento.setUsuario(usuario);
        movimiento.setFecha(LocalDateTime.now());
        movimientoRepository.save(movimiento);

        return toResponse(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecetaItemResponse> listRecetaByProducto(Long productoId) {
        return recetaRepository.findByProductoId(productoId).stream()
            .map(this::toResponse)
            .toList();
    }

    private InsumoResponse toResponse(Insumo insumo) {
        boolean lowStock = insumo.getStockActual().compareTo(insumo.getStockMinimo()) <= 0;
        return new InsumoResponse(insumo.getId(), insumo.getNombre(), insumo.getStockActual(), insumo.getStockMinimo(), insumo.getUnidadMedida().name(), lowStock);
    }

    private MovimientoInventarioResponse toResponse(MovimientoInventario movimiento) {
        return new MovimientoInventarioResponse(
            movimiento.getId(),
            movimiento.getInsumo().getNombre(),
            movimiento.getTipo().name(),
            movimiento.getCantidad(),
            movimiento.getMotivo(),
            movimiento.getFecha(),
            movimiento.getUsuario().getNombre());
    }

    private RecetaItemResponse toResponse(Receta receta) {
        return new RecetaItemResponse(
            receta.getInsumo().getId(),
            receta.getInsumo().getNombre(),
            receta.getCantidadRequerida(),
            receta.getInsumo().getUnidadMedida().name());
    }
}
