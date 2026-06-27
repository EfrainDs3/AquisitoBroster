package aquisito_broster_api.service.impl;

import aquisito_broster_api.dto.caja.CajaAperturaRequest;
import aquisito_broster_api.dto.caja.CajaCierreRequest;
import aquisito_broster_api.dto.caja.CajaEgresoRequest;
import aquisito_broster_api.dto.caja.CajaResponse;
import aquisito_broster_api.entity.CajaSesion;
import aquisito_broster_api.entity.EstadoCaja;
import aquisito_broster_api.entity.Usuario;
import aquisito_broster_api.exception.BadRequestException;
import aquisito_broster_api.exception.CajaCerradaException;
import aquisito_broster_api.exception.ResourceNotFoundException;
import aquisito_broster_api.repository.CajaSesionRepository;
import aquisito_broster_api.repository.UsuarioRepository;
import aquisito_broster_api.service.CajaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CajaServiceImpl implements CajaService {

    private final CajaSesionRepository cajaSesionRepository;
    private final UsuarioRepository usuarioRepository;

    public CajaServiceImpl(CajaSesionRepository cajaSesionRepository, UsuarioRepository usuarioRepository) {
        this.cajaSesionRepository = cajaSesionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public CajaResponse abrir(CajaAperturaRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsuarioUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        cajaSesionRepository.findFirstByUsuarioAndEstadoOrderByFechaAperturaDesc(usuario, EstadoCaja.ABIERTA)
            .ifPresent(session -> { throw new BadRequestException("Ya existe una caja abierta para este usuario"); });

        CajaSesion caja = new CajaSesion();
        caja.setUsuario(usuario);
        caja.setMontoApertura(request.getMontoApertura());
        caja.setIngresosVentas(BigDecimal.ZERO);
        caja.setEgresosAdicionales(BigDecimal.ZERO);
        caja.setEstado(EstadoCaja.ABIERTA);
        caja.setFechaApertura(LocalDateTime.now());
        return toResponse(cajaSesionRepository.save(caja));
    }

    @Override
    @Transactional
    public CajaResponse registrarEgreso(CajaEgresoRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsuarioUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        CajaSesion caja = cajaSesionRepository.findFirstByUsuarioAndEstadoOrderByFechaAperturaDesc(usuario, EstadoCaja.ABIERTA)
            .orElseThrow(() -> new CajaCerradaException("No existe una caja abierta"));

        caja.setEgresosAdicionales(caja.getEgresosAdicionales().add(request.getMonto()));
        return toResponse(cajaSesionRepository.save(caja));
    }

    @Override
    @Transactional
    public CajaResponse cerrar(CajaCierreRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsuarioUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        CajaSesion caja = cajaSesionRepository.findFirstByUsuarioAndEstadoOrderByFechaAperturaDesc(usuario, EstadoCaja.ABIERTA)
            .orElseThrow(() -> new CajaCerradaException("No existe una caja abierta"));

        BigDecimal cierre = caja.getMontoApertura().add(caja.getIngresosVentas()).subtract(caja.getEgresosAdicionales());
        caja.setEstado(EstadoCaja.CERRADA);
        caja.setFechaCierre(LocalDateTime.now());
        caja.setMontoCierre(cierre);
        return toResponse(cajaSesionRepository.save(caja));
    }

    @Override
    public CajaResponse estadoActual(String usuarioUsername) {
        Usuario usuario = usuarioRepository.findByUsername(usuarioUsername)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        CajaSesion caja = cajaSesionRepository.findFirstByUsuarioAndEstadoOrderByFechaAperturaDesc(usuario, EstadoCaja.ABIERTA)
            .orElseThrow(() -> new CajaCerradaException("No existe una caja abierta"));
        return toResponse(caja);
    }

    private CajaResponse toResponse(CajaSesion caja) {
        return new CajaResponse(
            caja.getId(),
            caja.getUsuario() != null ? caja.getUsuario().getNombre() : null,
            caja.getFechaApertura(),
            caja.getFechaCierre(),
            caja.getMontoApertura(),
            caja.getIngresosVentas(),
            caja.getEgresosAdicionales(),
            caja.getMontoCierre(),
            caja.getEstado()
        );
    }
}