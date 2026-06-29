package aquisito_broster_api.service.impl;

import aquisito_broster_api.dto.reporte.ReporteResumenResponse;
import aquisito_broster_api.entity.EstadoPedido;
import aquisito_broster_api.entity.TipoMovimiento;
import aquisito_broster_api.repository.InsumoRepository;
import aquisito_broster_api.repository.MovimientoInventarioRepository;
import aquisito_broster_api.repository.PedidoRepository;
import aquisito_broster_api.service.ReporteService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final PedidoRepository pedidoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final InsumoRepository insumoRepository;

    public ReporteServiceImpl(PedidoRepository pedidoRepository, MovimientoInventarioRepository movimientoRepository, InsumoRepository insumoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.movimientoRepository = movimientoRepository;
        this.insumoRepository = insumoRepository;
    }

    @Override
    public ReporteResumenResponse getResumenDiario() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        var pedidos = pedidoRepository.findByFechaBetweenOrderByFechaDesc(from, to);
        var movimientos = movimientoRepository.findByFechaBetween(from, to);

        BigDecimal ventasTotales = pedidos.stream()
            .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
            .map(p -> p.getTotal() == null ? BigDecimal.ZERO : p.getTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pedidosRegistrados = pedidos.size();
        long mermasRegistradas = movimientos.stream().filter(m -> m.getTipo() == TipoMovimiento.MERMA).count();
        BigDecimal ticketPromedio = pedidosRegistrados > 0
            ? ventasTotales.divide(BigDecimal.valueOf(pedidosRegistrados), 2, java.math.RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        long stockCritico = insumoRepository.findAll().stream()
            .filter(i -> i.getStockActual().compareTo(i.getStockMinimo()) <= 0)
            .count();

        Map<String, BigDecimal[]> ranking = new HashMap<>();
        pedidos.forEach(pedido -> pedido.getDetalles().forEach(detalle -> {
            String nombre = detalle.getProducto().getNombre();
            BigDecimal[] current = ranking.computeIfAbsent(nombre, key -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            current[0] = current[0].add(BigDecimal.valueOf(detalle.getCantidad()));
            current[1] = current[1].add(detalle.getSubtotal() == null ? BigDecimal.ZERO : detalle.getSubtotal());
        }));

        var topProductos = ranking.entrySet().stream()
            .sorted((a, b) -> b.getValue()[0].compareTo(a.getValue()[0]))
            .map(entry -> new ReporteResumenResponse.TopProducto(entry.getKey(), entry.getValue()[0].longValue(), entry.getValue()[1]))
            .toList();

        return new ReporteResumenResponse(ventasTotales, pedidosRegistrados, mermasRegistradas, ticketPromedio, stockCritico, topProductos);
    }
}