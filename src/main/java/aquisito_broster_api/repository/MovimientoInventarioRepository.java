package aquisito_broster_api.repository;

import aquisito_broster_api.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByFechaBetween(LocalDateTime from, LocalDateTime to);
}