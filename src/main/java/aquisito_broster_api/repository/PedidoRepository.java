package aquisito_broster_api.repository;

import aquisito_broster_api.entity.EstadoPedido;
import aquisito_broster_api.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByFechaBetweenOrderByFechaDesc(LocalDateTime from, LocalDateTime to);

    List<Pedido> findByEstado(EstadoPedido estado);
}