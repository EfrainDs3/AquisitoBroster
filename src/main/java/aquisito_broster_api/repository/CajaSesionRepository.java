package aquisito_broster_api.repository;

import aquisito_broster_api.entity.CajaSesion;
import aquisito_broster_api.entity.EstadoCaja;
import aquisito_broster_api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CajaSesionRepository extends JpaRepository<CajaSesion, Long> {

    Optional<CajaSesion> findFirstByUsuarioAndEstadoOrderByFechaAperturaDesc(Usuario usuario, EstadoCaja estado);

    Optional<CajaSesion> findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja estado);
}