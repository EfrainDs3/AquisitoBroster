-- ==========================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS
-- PROYECTO: AQUICITO BROASTER
-- CURSO: INGENIERÍA DE REQUERIMIENTOS
-- TECNOLOGÍAS: MySQL, Spring Boot, React
-- ==========================================

CREATE DATABASE IF NOT EXISTS bd_aquicitobroaster;
USE bd_aquicitobroaster;

-- 1. TABLA: usuarios
-- Guarda las credenciales y roles para el control de accesos.
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Almacenada como hash (Spring Security BCrypt)
    nombre VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN', 'CAJERO', 'COCINA', 'ALMACEN') NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. TABLA: productos
-- Platos, alitas, combos familiares que se ofrecen al cliente.
CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. TABLA: insumos
-- Ingredientes crudos en inventario cuyo stock se controla.
CREATE TABLE IF NOT EXISTS insumos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    stock_actual DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    stock_minimo DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    unidad_medida ENUM('KG', 'UNIDAD', 'LITRO', 'PORCION') NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. TABLA: recetas (Relación N:M entre productos e insumos)
-- Determina qué insumos se descuentan al vender un producto específico.
CREATE TABLE IF NOT EXISTS recetas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT NOT NULL,
    insumo_id INT NOT NULL,
    cantidad_requerida DECIMAL(10, 3) NOT NULL, -- Cantidad exacta de insumo usada en el producto
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    FOREIGN KEY (insumo_id) REFERENCES insumos(id) ON DELETE CASCADE,
    UNIQUE KEY prod_insumo_unique (producto_id, insumo_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. TABLA: movimientos_inventario
-- Registra entradas de insumos (compras), salidas (ventas) y mermas (desperdicios/deterioro).
CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    insumo_id INT NOT NULL,
    tipo ENUM('ENTRADA', 'SALIDA', 'MERMA') NOT NULL,
    cantidad DECIMAL(10, 2) NOT NULL,
    motivo VARCHAR(255) NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_id INT NOT NULL,
    FOREIGN KEY (insumo_id) REFERENCES insumos(id) ON DELETE RESTRICT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. TABLA: pedidos
-- Registro general de ventas/pedidos de la mesa o llevar.
CREATE TABLE IF NOT EXISTS pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cliente VARCHAR(100) DEFAULT 'Cliente General',
    estado ENUM('PENDIENTE', 'EN_PREPARACION', 'ENTREGADO', 'CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
    total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    usuario_id INT NOT NULL, -- Cajero que lo registra
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. TABLA: detalle_pedidos
-- Los ítems específicos comprados dentro de cada pedido.
CREATE TABLE IF NOT EXISTS detalle_pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. TABLA: caja_sesiones
-- Control de caja diario. Registra los turnos de los cajeros.
CREATE TABLE IF NOT EXISTS caja_sesiones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    fecha_apertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP NULL DEFAULT NULL,
    monto_apertura DECIMAL(10, 2) NOT NULL,
    ingresos_ventas DECIMAL(10, 2) DEFAULT 0.00,
    egresos_adicionales DECIMAL(10, 2) DEFAULT 0.00, -- egresos directos de caja por compras urgentes
    monto_cierre DECIMAL(10, 2) NULL DEFAULT NULL,
    estado ENUM('ABIERTA', 'CERRADA') NOT NULL DEFAULT 'ABIERTA',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ==========================================
-- INSERTANDO DATOS INICIALES (SEED DATA)
-- ==========================================

-- Usuarios de prueba (Contraseñas de ejemplo, en prod deben hashearse con BCrypt)
INSERT INTO usuarios (username, password, nombre, rol, activo) VALUES
('admin', '$2a$10$X.p1t7eH38GjM4hYl9RkHux1YcEq7YvA64D7VwV1bfe6x2g3h/7tG', 'Santiago Torres (Propietario)', 'ADMIN', 1),
('cajero1', '$2a$10$X.p1t7eH38GjM4hYl9RkHux1YcEq7YvA64D7VwV1bfe6x2g3h/7tG', 'Jarry Vásquez (Cajero)', 'CAJERO', 1),
('cocinero1', '$2a$10$X.p1t7eH38GjM4hYl9RkHux1YcEq7YvA64D7VwV1bfe6x2g3h/7tG', 'Danny Pezo (Chef)', 'COCINA', 1),
('almacen1', '$2a$10$X.p1t7eH38GjM4hYl9RkHux1YcEq7YvA64D7VwV1bfe6x2g3h/7tG', 'Jheison Carranza (Almacenero)', 'ALMACEN', 1);

-- Insumos iniciales
INSERT INTO insumos (nombre, stock_actual, stock_minimo, unidad_medida) VALUES
('Pollo Trozado (Presas)', 150.00, 30.00, 'UNIDAD'),
('Papas Peladas / Cortadas', 80.00, 20.00, 'KG'),
('Arroz Chaufa Preparado', 40.00, 10.00, 'KG'),
('Aceite Vegetal', 50.00, 10.00, 'LITRO'),
('Crema Mayonesa (Salsas)', 15.00, 5.00, 'LITRO'),
('Crema Ketchup', 12.00, 4.00, 'LITRO'),
('Gaseosa de 1.5L', 60.00, 15.00, 'UNIDAD');

-- Productos iniciales
INSERT INTO productos (nombre, descripcion, precio, disponible) VALUES
('1/4 de Pollo Broaster', '1 presa de pollo broaster crujiente + papas fritas + ensalada + cremas', 15.00, 1),
('1/2 de Pollo Broaster', '2 presas de pollo broaster crujiente + doble papas fritas + ensalada + cremas', 28.00, 1),
('Combo Familiar Broaster', '8 presas de pollo broaster + 1kg de papas fritas + ensalada familiar + gaseosa 1.5L', 85.00, 1),
('Alitas Broaster (6 unidades)', '6 alitas crujientes + papas fritas + cremas', 18.00, 1),
('Chaufa con Broaster', 'Arroz chaufa de la casa servido con 1 presa de pollo broaster', 20.00, 1);

-- Recetas asociadas a cada producto para el descuento de inventario
INSERT INTO recetas (producto_id, insumo_id, cantidad_requerida) VALUES
-- 1/4 de Pollo Broaster usa: 1 Presa de Pollo, 0.25 KG de papas, 0.1 LITRO de aceite, 0.05 LITRO de mayonesa y kétchup.
(1, 1, 1.000), -- 1 presa
(1, 2, 0.250), -- 0.250 kg papas
(1, 4, 0.100), -- 0.100 L aceite
(1, 5, 0.050), -- 0.050 L mayonesa

-- 1/2 de Pollo Broaster usa: 2 Presas de Pollo, 0.50 KG de papas, 0.2 LITRO de aceite, 0.08 LITRO de mayonesa
(2, 1, 2.000),
(2, 2, 0.500),
(2, 4, 0.200),
(2, 5, 0.080),

-- Combo Familiar usa: 8 Presas de Pollo, 1 KG de papas, 0.5 LITRO de aceite, 1 Gaseosa, 0.15 LITRO de mayonesa
(3, 1, 8.000),
(3, 2, 1.000),
(3, 4, 0.500),
(3, 5, 0.150),
(3, 7, 1.000),

-- Alitas Broaster (6 u) usa: 6 presas de alitas (simulado aquí en Presas de Pollo como 3 unidades), 0.25 KG de papas
(4, 1, 3.000),
(4, 2, 0.250),
(4, 4, 0.100),

-- Chaufa con Broaster usa: 1 Presa de Pollo, 0.3 KG de Arroz Chaufa, 0.05 L de aceite
(5, 1, 1.000),
(5, 3, 0.300),
(5, 4, 0.050);
