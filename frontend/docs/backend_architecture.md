# Arquitectura del Backend: Spring Boot
**Proyecto:** Sistema de Gestión de Ventas e Inventario - Aquicito Broaster
**Tecnologías principales:** Java 17+, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA, Hibernate, MySQL.

---

## 1. Estructura de Paquetes (Package Layout)

Se propone una arquitectura multicapa estándar y limpia orientada a componentes:

```
com.aquicitobroaster.api
│
├── config                 # Configuración de Seguridad (JWT), CORS, Auditoría y BD.
│   ├── SecurityConfig.java
│   ├── JwtAuthenticationFilter.java
│   └── WebMvcConfig.java
│
├── controller             # Controladores REST que exponen los endpoints.
│   ├── AuthController.java
│   ├── CajaController.java
│   ├── InventarioController.java
│   ├── PedidoController.java
│   ├── ProductoController.java
│   └── ReporteController.java
│
├── dto                    # Objetos de Transferencia de Datos (Request/Response).
│   ├── request
│   │   ├── LoginRequest.java
│   │   ├── PedidoCreateRequest.java
│   │   ├── InsumoMovimientoRequest.java
│   │   └── CajaAperturaRequest.java
│   └── response
│       ├── AuthResponse.java
│       ├── PedidoDTO.java
│       ├── InsumoStockDTO.java
│       └── ReporteVentasDTO.java
│
├── entity                 # Entidades JPA correspondientes a las tablas MySQL.
│   ├── CajaSesion.java
│   ├── DetallePedido.java
│   ├── Insumo.java
│   ├── MovimientoInventario.java
│   ├── Pedido.java
│   ├── Producto.java
│   ├── Receta.java
│   └── Usuario.java
│
├── exception              # Manejador de excepciones global y personalizadas.
│   ├── GlobalExceptionHandler.java
│   ├── InsufficientStockException.java
│   ├── CajaCerradaException.java
│   └── ResourceNotFoundException.java
│
├── repository             # Interfaces Spring Data JPA para acceso a datos.
│   ├── CajaSesionRepository.java
│   ├── InsumoRepository.java
│   ├── MovimientoInventarioRepository.java
│   ├── PedidoRepository.java
│   ├── ProductoRepository.java
│   └── UsuarioRepository.java
│
└── service                # Lógica de negocio (Servicios e Interfaces).
    ├── impl
    │   ├── AuthServiceImpl.java
    │   ├── CajaServiceImpl.java
    │   ├── InventarioServiceImpl.java
    │   ├── PedidoServiceImpl.java
    │   └── ReporteServiceImpl.java
    ├── CajaService.java
    ├── InventarioService.java
    ├── PedidoService.java
    └── ReporteService.java
```

---

## 2. Flujo Crítico: Registro de Pedido y Descuento de Insumos

El núcleo operativo del sistema es garantizar que cuando se registra una venta, los insumos de su receta asociada se descuenten automáticamente de forma **atómica**. Si algún insumo no tiene suficiente stock, la transacción debe fallar por completo (Rollback).

### Algoritmo en `PedidoServiceImpl.java` (Ejemplo de Lógica):
```java
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final CajaSesionRepository cajaRepository;

    @Override
    @Transactional // Garantiza la atomicidad de la venta y descuento de stock
    public PedidoDTO registrarPedido(PedidoCreateRequest request, Usuario cajero) {
        
        // 1. Validar que la caja esté abierta para el día/turno
        CajaSesion cajaActiva = cajaRepository.findByEstadoAndUsuario(EstadoCaja.ABIERTA, cajero)
            .orElseThrow(() -> new CajaCerradaException("Debe abrir caja antes de registrar pedidos."));

        Pedido pedido = new Pedido();
        pedido.setCliente(request.getCliente());
        pedido.setUsuario(cajero);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        
        double total = 0.0;
        List<DetallePedido> detalles = new ArrayList<>();

        for (DetalleItemRequest item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            // Validar recetas y descontar insumos en base a la cantidad comprada
            for (Receta receta : producto.getRecetas()) {
                Insumo insumo = receta.getInsumo();
                double cantidadADescuentar = receta.getCantidadRequerida() * item.getCantidad();

                if (insumo.getStockActual() < cantidadADescuentar) {
                    throw new InsufficientStockException("Stock insuficiente de: " + insumo.getNombre() 
                        + ". Disponible: " + insumo.getStockActual() + " " + insumo.getUnidadMedida());
                }

                // Descontar stock y guardar
                insumo.setStockActual(insumo.getStockActual() - cantidadADescuentar);
                insumoRepository.save(insumo);

                // Registrar movimiento de inventario (Salida automática por venta)
                MovimientoInventario movimiento = new MovimientoInventario();
                movimiento.setInsumo(insumo);
                movimiento.setTipo(TipoMovimiento.SALIDA);
                movimiento.setCantidad(cantidadADescuentar);
                movimiento.setMotivo("Venta automática - Pedido #" + pedido.getId());
                movimiento.setUsuario(cajero);
                movimientoRepository.save(movimiento);
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio() * item.getCantidad());
            
            total += detalle.getSubtotal();
            detalles.add(detalle);
        }

        pedido.setDetalles(detalles);
        pedido.setTotal(total);
        
        // Sumar ingresos a la sesión de caja activa
        cajaActiva.setIngresosVentas(cajaActiva.getIngresosVentas() + total);
        cajaRepository.save(cajaActiva);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return convertToDTO(pedidoGuardado);
    }
}
```

---

## 3. Especificación de Endpoints REST (API Docs)

### A. Autenticación (`/api/auth`)
*   `POST /api/auth/login`
    *   **Permiso:** Público.
    *   **Request Body:** `LoginRequest` (username, password)
    *   **Response:** `AuthResponse` (JWT Token, nombre, rol, username)

### B. Ventas y Pedidos (`/api/pedidos`)
*   `POST /api/pedidos`
    *   **Permiso:** `ADMIN`, `CAJERO`
    *   **Descripción:** Registra un nuevo pedido y descuenta stock.
    *   **Response:** DTO del pedido creado.
*   `GET /api/pedidos`
    *   **Permiso:** `ADMIN`, `CAJERO`, `COCINA`
    *   **Descripción:** Lista todos los pedidos del día. Soporta filtrado por estado (`PENDIENTE`, `EN_PREPARACION`, etc.).
*   `PATCH /api/pedidos/{id}/estado`
    *   **Permiso:** `ADMIN`, `CAJERO`, `COCINA`
    *   **Request Body:** `{ "estado": "EN_PREPARACION" }`
    *   **Descripción:** Cambia el estado del pedido (cocina actualiza a preparación/entregado).

### C. Caja (`/api/caja`)
*   `GET /api/caja/estado`
    *   **Permiso:** `ADMIN`, `CAJERO`
    *   **Descripción:** Verifica si el cajero actual tiene una caja abierta y su balance parcial.
*   `POST /api/caja/apertura`
    *   **Permiso:** `ADMIN`, `CAJERO`
    *   **Request Body:** `{ "montoApertura": 150.00 }`
*   `POST /api/caja/egreso`
    *   **Permiso:** `ADMIN`, `CAJERO`
    *   **Request Body:** `{ "monto": 35.00, "motivo": "Compra de cilantro de emergencia" }`
*   `POST /api/caja/cierre`
    *   **Permiso:** `ADMIN`, `CAJERO`
    *   **Descripción:** Realiza el cuadre final de caja del turno y la cierra.

### D. Inventario (`/api/inventario`)
*   `GET /api/inventario/insumos`
    *   **Permiso:** `ADMIN`, `ALMACEN`
    *   **Descripción:** Obtiene los insumos con su stock actual, unidad de medida y estado de alerta de stock mínimo.
*   `POST /api/inventario/movimientos`
    *   **Permiso:** `ADMIN`, `ALMACEN`
    *   **Request Body:** `{ "insumoId": 1, "tipo": "ENTRADA", "cantidad": 50, "motivo": "Compra lote semanal" }`

### E. Reportes (`/api/reportes`)
*   `GET /api/reportes/resumen-diario`
    *   **Permiso:** `ADMIN`
    *   **Descripción:** Retorna ventas totales del día, mermas registradas, balance de caja y nivel de criticidad del inventario.
