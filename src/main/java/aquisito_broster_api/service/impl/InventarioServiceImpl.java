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

        insumo.setStockActual(newStock);
        insumoRepository.save(insumo);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setInsumo(insumo);
        movimiento.setTipo(request.getTipo());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setMotivo(request.getMotivo());
        movimiento.setUsuario(usuario);
        movimientoRepository.save(movimiento);

        return toResponse(movimiento);
    }

    @Override
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
}package aquisito_broster_api.service.impl;

import aquisito_broster_api.dto.inventario.InsumoResponse;
import aquisito_broster_api.dto.inventario.MovimientoInventarioRequest;
import aquisito_broster_api.entity.Insumo;
import aquisito_broster_api.entity.MovimientoInventario;
import aquisito_broster_api.entity.Usuario;
import aquisito_broster_api.exception.ResourceNotFoundException;
import aquisito_broster_api.repository.InsumoRepository;
import aquisito_broster_api.repository.MovimientoInventarioRepository;
import aquisito_broster_api.repository.UsuarioRepository;
import aquisito_broster_api.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    public InventarioServiceImpl(InsumoRepository insumoRepository, MovimientoInventarioRepository movimientoRepository, UsuarioRepository usuarioRepository) {
        this.insumoRepository = insumoRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<InsumoResponse> listarInsumos() {
        return insumoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public MovimientoInventario registrarMovimiento(MovimientoInventarioRequest request) {
        Insumo insumo = insumoRepository.findById(request.getInsumoId())
            .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado"));
        Usuario usuario = usuarioRepository.findByUsername(request.getUsuarioUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        BigDecimal nuevaCantidad = request.getTipo() == aquisito_broster_api.entity.TipoMovimiento.ENTRADA
            ? insumo.getStockActual().add(request.getCantidad())
            : insumo.getStockActual().subtract(request.getCantidad());

        if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResourceNotFoundException("Stock insuficiente para registrar el movimiento");
        }

        insumo.setStockActual(nuevaCantidad);
        insumoRepository.save(insumo);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setInsumo(insumo);
        movimiento.setTipo(request.getTipo());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setMotivo(request.getMotivo());
        movimiento.setUsuario(usuario);
        movimiento.setFecha(LocalDateTime.now());
        return movimientoRepository.save(movimiento);
    }

    @Override
    public List<MovimientoInventario> listarMovimientosDelDia() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to = from.plusDays(1);
        return movimientoRepository.findByFechaBetween(from, to);
    }

    private InsumoResponse toResponse(Insumo insumo) {
        return new InsumoResponse(
            insumo.getId(),
            insumo.getNombre(),
            insumo.getStockActual(),
            insumo.getStockMinimo(),
            insumo.getUnidadMedida().name(),
            insumo.getStockActual().compareTo(insumo.getStockMinimo()) <= 0
        );
    }
}