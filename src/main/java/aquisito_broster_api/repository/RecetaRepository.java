package aquisito_broster_api.repository;

import aquisito_broster_api.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {

    @Query("select r from Receta r join fetch r.insumo where r.producto.id = :productoId")
    List<Receta> findByProductoId(@Param("productoId") Long productoId);
}
