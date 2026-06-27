package aquisito_broster_api.config;

import aquisito_broster_api.entity.CajaSesion;
import aquisito_broster_api.entity.EstadoCaja;
import aquisito_broster_api.entity.Insumo;
import aquisito_broster_api.entity.Producto;
import aquisito_broster_api.entity.Receta;
import aquisito_broster_api.entity.Role;
import aquisito_broster_api.entity.UnidadMedida;
import aquisito_broster_api.entity.Usuario;
import aquisito_broster_api.repository.CajaSesionRepository;
import aquisito_broster_api.repository.InsumoRepository;
import aquisito_broster_api.repository.ProductoRepository;
import aquisito_broster_api.repository.RecetaRepository;
import aquisito_broster_api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(UsuarioRepository usuarioRepository,
                               ProductoRepository productoRepository,
                               InsumoRepository insumoRepository,
                               RecetaRepository recetaRepository,
                               CajaSesionRepository cajaSesionRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                Usuario admin = createUser("admin", "admin123", "Santiago Torres (Propietario)", Role.ADMIN, passwordEncoder);
                Usuario cajero = createUser("cajero1", "cajero123", "Jarry Vásquez (Cajero)", Role.CAJERO, passwordEncoder);
                Usuario cocina = createUser("cocinero1", "cocina123", "Danny Pezo (Chef)", Role.COCINA, passwordEncoder);
                Usuario almacen = createUser("almacen1", "almacen123", "Jheison Carranza (Almacenero)", Role.ALMACEN, passwordEncoder);
                usuarioRepository.saveAll(List.of(admin, cajero, cocina, almacen));
            }

            if (insumoRepository.count() == 0) {
                Insumo i1 = createInsumo("Pollo Trozado (Presas)", "150.00", "30.00", UnidadMedida.UNIDAD);
                Insumo i2 = createInsumo("Papas Peladas / Cortadas", "80.00", "20.00", UnidadMedida.KG);
                Insumo i3 = createInsumo("Arroz Chaufa Preparado", "40.00", "10.00", UnidadMedida.KG);
                Insumo i4 = createInsumo("Aceite Vegetal", "50.00", "10.00", UnidadMedida.LITRO);
                Insumo i5 = createInsumo("Crema Mayonesa (Salsas)", "15.00", "5.00", UnidadMedida.LITRO);
                Insumo i6 = createInsumo("Crema Ketchup", "12.00", "4.00", UnidadMedida.LITRO);
                Insumo i7 = createInsumo("Gaseosa de 1.5L", "60.00", "15.00", UnidadMedida.UNIDAD);
                insumoRepository.saveAll(List.of(i1, i2, i3, i4, i5, i6, i7));
            }

            if (productoRepository.count() == 0) {
                Producto p1 = createProducto("1/4 de Pollo Broaster", "1 presa de pollo broaster crujiente + papas fritas + ensalada + cremas", "15.00", "individuales");
                Producto p2 = createProducto("1/2 de Pollo Broaster", "2 presas de pollo broaster crujiente + doble papas fritas + ensalada + cremas", "28.00", "individuales");
                Producto p3 = createProducto("Combo Familiar Broaster", "8 presas de pollo broaster + 1kg de papas fritas + ensalada familiar + gaseosa 1.5L", "85.00", "combos");
                Producto p4 = createProducto("Alitas Broaster (6 unidades)", "6 alitas crujientes + papas fritas + cremas", "18.00", "individuales");
                Producto p5 = createProducto("Chaufa con Broaster", "Arroz chaufa de la casa servido con 1 presa de pollo broaster", "20.00", "combos");
                productoRepository.saveAll(List.of(p1, p2, p3, p4, p5));
            }

            if (recetaRepository.count() == 0) {
                List<Producto> productos = productoRepository.findAll();
                List<Insumo> insumos = insumoRepository.findAll();
                recetaRepository.saveAll(List.of(
                    receta(productos.get(0), insumos.get(0), "1.000"),
                    receta(productos.get(0), insumos.get(1), "0.250"),
                    receta(productos.get(0), insumos.get(3), "0.100"),
                    receta(productos.get(0), insumos.get(4), "0.050"),

                    receta(productos.get(1), insumos.get(0), "2.000"),
                    receta(productos.get(1), insumos.get(1), "0.500"),
                    receta(productos.get(1), insumos.get(3), "0.200"),
                    receta(productos.get(1), insumos.get(4), "0.080"),

                    receta(productos.get(2), insumos.get(0), "8.000"),
                    receta(productos.get(2), insumos.get(1), "1.000"),
                    receta(productos.get(2), insumos.get(3), "0.500"),
                    receta(productos.get(2), insumos.get(4), "0.150"),
                    receta(productos.get(2), insumos.get(6), "1.000"),

                    receta(productos.get(3), insumos.get(0), "3.000"),
                    receta(productos.get(3), insumos.get(1), "0.250"),
                    receta(productos.get(3), insumos.get(3), "0.100"),

                    receta(productos.get(4), insumos.get(0), "1.000"),
                    receta(productos.get(4), insumos.get(2), "0.300"),
                    receta(productos.get(4), insumos.get(3), "0.050")
                ));
            }

            if (cajaSesionRepository.count() == 0) {
                Usuario cajero = usuarioRepository.findByUsername("cajero1").orElseThrow();
                CajaSesion caja = new CajaSesion();
                caja.setUsuario(cajero);
                caja.setMontoApertura(new BigDecimal("150.00"));
                caja.setIngresosVentas(new BigDecimal("480.00"));
                caja.setEgresosAdicionales(new BigDecimal("35.00"));
                caja.setMontoCierre(new BigDecimal("595.00"));
                caja.setEstado(EstadoCaja.CERRADA);
                cajaSesionRepository.save(caja);
            }
        };
    }

    private Usuario createUser(String username, String rawPassword, String nombre, Role rol, PasswordEncoder passwordEncoder) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(rawPassword));
        usuario.setNombre(nombre);
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuario;
    }

    private Insumo createInsumo(String nombre, String stockActual, String stockMinimo, UnidadMedida unidadMedida) {
        Insumo insumo = new Insumo();
        insumo.setNombre(nombre);
        insumo.setStockActual(new BigDecimal(stockActual));
        insumo.setStockMinimo(new BigDecimal(stockMinimo));
        insumo.setUnidadMedida(unidadMedida);
        return insumo;
    }

    private Producto createProducto(String nombre, String descripcion, String precio, String categoria) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(new BigDecimal(precio));
        producto.setCategoria(categoria);
        producto.setDisponible(true);
        return producto;
    }

    private Receta receta(Producto producto, Insumo insumo, String cantidad) {
        Receta receta = new Receta();
        receta.setProducto(producto);
        receta.setInsumo(insumo);
        receta.setCantidadRequerida(new BigDecimal(cantidad));
        return receta;
    }
}