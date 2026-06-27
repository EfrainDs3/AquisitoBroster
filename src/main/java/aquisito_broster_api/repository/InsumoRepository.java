package aquisito_broster_api.repository;

import aquisito_broster_api.entity.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    List<Insumo> findByStockActualLessThanEqual(java.math.BigDecimal stockMinimo);
}