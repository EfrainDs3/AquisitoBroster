package aquisito_broster_api.service;

import aquisito_broster_api.dto.caja.CajaAperturaRequest;
import aquisito_broster_api.dto.caja.CajaCierreRequest;
import aquisito_broster_api.dto.caja.CajaEgresoRequest;
import aquisito_broster_api.dto.caja.CajaResponse;

public interface CajaService {

    CajaResponse abrir(CajaAperturaRequest request);

    CajaResponse registrarEgreso(CajaEgresoRequest request);

    CajaResponse cerrar(CajaCierreRequest request);

    CajaResponse estadoActual(String usuarioUsername);
}