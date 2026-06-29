package aquisito_broster_api.service.impl;

import aquisito_broster_api.dto.request.EstadoPedidoRequest;
import aquisito_broster_api.dto.request.PedidoCreateRequest;
import aquisito_broster_api.dto.response.DetallePedidoResponse;
import aquisito_broster_api.dto.response.PedidoResponse;
import aquisito_broster_api.entity.CajaSesion;
import aquisito_broster_api.entity.DetallePedido;
import aquisito_broster_api.entity.EstadoCaja;
import aquisito_broster_api.entity.EstadoPedido;
import aquisito_broster_api.entity.Insumo;
import aquisito_broster_api.entity.MovimientoInventario;
import aquisito_broster_api.entity.Pedido;
import aquisito_broster_api.entity.Producto;
import aquisito_broster_api.entity.Receta;
import aquisito_broster_api.entity.TipoMovimiento;
import aquisito_broster_api.entity.Usuario;
import aquisito_broster_api.exception.CajaCerradaException;
import aquisito_broster_api.exception.InsufficientStockException;
import aquisito_broster_api.exception.ResourceNotFoundException;
import aquisito_broster_api.repository.CajaSesionRepository;
import aquisito_broster_api.repository.InsumoRepository;
import aquisito_broster_api.repository.MovimientoInventarioRepository;
import aquisito_broster_api.repository.PedidoRepository;
import aquisito_broster_api.repository.ProductoRepository;
import aquisito_broster_api.repository.RecetaRepository;
import aquisito_broster_api.repository.UsuarioRepository;
import aquisito_broster_api.service.PedidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final RecetaRepository recetaRepository;
    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final CajaSesionRepository cajaSesionRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository, ProductoRepository productoRepository, RecetaRepository recetaRepository, InsumoRepository insumoRepository, MovimientoInventarioRepository movimientoRepository, CajaSesionRepository cajaSesionRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.recetaRepository = recetaRepository;
        this.insumoRepository = insumoRepository;
        this.movimientoRepository = movimientoRepository;
        this.cajaSesionRepository = cajaSesionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public PedidoResponse registerOrder(PedidoCreateRequest request, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        CajaSesion caja = cajaSesionRepository.findFirstByUsuarioAndEstadoOrderByFechaAperturaDesc(usuario, EstadoCaja.ABIERTA)
            .orElseThrow(() -> new CajaCerradaException("Debe abrir caja antes de registrar pedidos"));

        Pedido pedido = new Pedido();
        pedido.setCliente(request.getCliente());
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFecha(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        List<DetallePedido> detalles = new ArrayList<>();

        for (var item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            List<Receta> recetas = recetaRepository.findByProductoId(producto.getId());
            for (Receta receta : recetas) {
                Insumo insumo = receta.getInsumo();
                BigDecimal required = receta.getCantidadRequerida().multiply(BigDecimal.valueOf(item.getCantidad()));

                if (insumo.getStockActual().compareTo(required) < 0) {
                    throw new InsufficientStockException("Stock insuficiente de: " + insumo.getNombre());
                }

                insumo.setStockActual(insumo.getStockActual().subtract(required));
                insumoRepository.save(insumo);

                MovimientoInventario movimiento = new MovimientoInventario();
                movimiento.setInsumo(insumo);
                movimiento.setTipo(TipoMovimiento.SALIDA);
                movimiento.setCantidad(required);
                movimiento.setMotivo("Venta automática - pedido pendiente");
                movimiento.setUsuario(usuario);
                movimientoRepository.save(movimiento);
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
            detalles.add(detalle);
            total = total.add(detalle.getSubtotal());
        }

        pedido.setTotal(total);
        pedido.setDetalles(detalles);
        Pedido saved = pedidoRepository.save(pedido);

        caja.setIngresosVentas(caja.getIngresosVentas().add(total));
        cajaSesionRepository.save(caja);

        return toResponse(saved);
    }

    @Override
    public List<PedidoResponse> listOrders(EstadoPedido estado) {
        List<Pedido> pedidos = estado == null
            ? pedidoRepository.findAll()
            : pedidoRepository.findByEstado(estado);

        return pedidos.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public PedidoResponse updateOrderState(Long pedidoId, EstadoPedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        pedido.setEstado(request.getEstado());
        return toResponse(pedidoRepository.save(pedido));
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<DetallePedidoResponse> items = pedido.getDetalles().stream()
            .map(detalle -> new DetallePedidoResponse(
                detalle.getProducto().getId(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()))
            .toList();

        return new PedidoResponse(
            pedido.getId(),
            pedido.getFecha(),
            pedido.getCliente(),
            pedido.getEstado().name(),
            pedido.getTotal(),
            items);
    }
}