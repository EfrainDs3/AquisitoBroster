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
public class DataSeeder {

    @Bean
    public CommandLineRunner seed(
        UsuarioRepository usuarioRepository,
        ProductoRepository productoRepository,
        InsumoRepository insumoRepository,
        RecetaRepository recetaRepository,
        CajaSesionRepository cajaSesionRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                usuarioRepository.saveAll(List.of(
                    createUser("admin", "admin123", "Santiago Torres (Propietario)", Role.ADMIN, passwordEncoder),
                    createUser("cajero1", "cajero123", "Jarry Vásquez (Cajero)", Role.CAJERO, passwordEncoder),
                    createUser("cocinero1", "cocina123", "Danny Pezo (Chef)", Role.COCINA, passwordEncoder),
                    createUser("almacen1", "almacen123", "Jheison Carranza (Almacenero)", Role.ALMACEN, passwordEncoder)
                ));
            }

            if (insumoRepository.count() == 0) {
                insumoRepository.saveAll(List.of(
                    createInsumo("Pollo Trozado (Presas)", "150.00", "30.00", UnidadMedida.UNIDAD),
                    createInsumo("Papas Peladas / Cortadas", "80.00", "20.00", UnidadMedida.KG),
                    createInsumo("Arroz Chaufa Preparado", "40.00", "10.00", UnidadMedida.KG),
                    createInsumo("Aceite Vegetal", "50.00", "10.00", UnidadMedida.LITRO),
                    createInsumo("Crema Mayonesa (Salsas)", "15.00", "5.00", UnidadMedida.LITRO),
                    createInsumo("Crema Ketchup", "12.00", "4.00", UnidadMedida.LITRO),
                    createInsumo("Gaseosa de 1.5L", "60.00", "15.00", UnidadMedida.UNIDAD)
                ));
            }

            if (productoRepository.count() == 0) {
                productoRepository.saveAll(List.of(
                    createProducto("1/4 de Pollo Broaster", "1 presa de pollo broaster crujiente + papas fritas + ensalada + cremas", "15.00", "individuales"),
                    createProducto("1/2 de Pollo Broaster", "2 presas de pollo broaster crujiente + doble papas fritas + ensalada + cremas", "28.00", "individuales"),
                    createProducto("Combo Familiar Broaster", "8 presas de pollo broaster + 1kg de papas fritas + ensalada familiar + gaseosa 1.5L", "85.00", "combos"),
                    createProducto("Alitas Broaster (6 unidades)", "6 alitas crujientes + papas fritas + cremas", "18.00", "individuales"),
                    createProducto("Chaufa con Broaster", "Arroz chaufa de la casa servido con 1 presa de pollo broaster", "20.00", "combos")
                ));
            }

            if (recetaRepository.count() == 0) {
                Producto p1 = productoRepository.findAll().stream().filter(p -> p.getNombre().equals("1/4 de Pollo Broaster")).findFirst().orElseThrow();
                Producto p2 = productoRepository.findAll().stream().filter(p -> p.getNombre().equals("1/2 de Pollo Broaster")).findFirst().orElseThrow();
                Producto p3 = productoRepository.findAll().stream().filter(p -> p.getNombre().equals("Combo Familiar Broaster")).findFirst().orElseThrow();
                Producto p4 = productoRepository.findAll().stream().filter(p -> p.getNombre().equals("Alitas Broaster (6 unidades)")).findFirst().orElseThrow();
                Producto p5 = productoRepository.findAll().stream().filter(p -> p.getNombre().equals("Chaufa con Broaster")).findFirst().orElseThrow();

                Insumo i1 = insumoRepository.findAll().stream().filter(i -> i.getNombre().equals("Pollo Trozado (Presas)")).findFirst().orElseThrow();
                Insumo i2 = insumoRepository.findAll().stream().filter(i -> i.getNombre().equals("Papas Peladas / Cortadas")).findFirst().orElseThrow();
                Insumo i3 = insumoRepository.findAll().stream().filter(i -> i.getNombre().equals("Arroz Chaufa Preparado")).findFirst().orElseThrow();
                Insumo i4 = insumoRepository.findAll().stream().filter(i -> i.getNombre().equals("Aceite Vegetal")).findFirst().orElseThrow();
                Insumo i5 = insumoRepository.findAll().stream().filter(i -> i.getNombre().equals("Crema Mayonesa (Salsas)")).findFirst().orElseThrow();
                Insumo i7 = insumoRepository.findAll().stream().filter(i -> i.getNombre().equals("Gaseosa de 1.5L")).findFirst().orElseThrow();

                recetaRepository.saveAll(List.of(
                    recipe(p1, i1, "1.000"), recipe(p1, i2, "0.250"), recipe(p1, i4, "0.100"), recipe(p1, i5, "0.050"),
                    recipe(p2, i1, "2.000"), recipe(p2, i2, "0.500"), recipe(p2, i4, "0.200"), recipe(p2, i5, "0.080"),
                    recipe(p3, i1, "8.000"), recipe(p3, i2, "1.000"), recipe(p3, i4, "0.500"), recipe(p3, i5, "0.150"), recipe(p3, i7, "1.000"),
                    recipe(p4, i1, "3.000"), recipe(p4, i2, "0.250"), recipe(p4, i4, "0.100"),
                    recipe(p5, i1, "1.000"), recipe(p5, i3, "0.300"), recipe(p5, i4, "0.050")
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

    private Usuario createUser(String username, String rawPassword, String nombre, Role role, PasswordEncoder encoder) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(encoder.encode(rawPassword));
        usuario.setNombre(nombre);
        usuario.setRol(role);
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

    private Receta recipe(Producto producto, Insumo insumo, String cantidad) {
        Receta receta = new Receta();
        receta.setProducto(producto);
        receta.setInsumo(insumo);
        receta.setCantidadRequerida(new BigDecimal(cantidad));
        return receta;
    }
}