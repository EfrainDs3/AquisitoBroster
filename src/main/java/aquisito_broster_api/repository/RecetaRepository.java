package aquisito_broster_api.repository;

import aquisito_broster_api.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {

    List<Receta> findByProductoId(Long productoId);
}