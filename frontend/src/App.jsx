import React, { useState, useEffect } from 'react';
import { 
  Flame, 
  Landmark, 
  BarChart3, 
  RotateCcw, 
  Drumstick, 
  AlertTriangle, 
  Bell, 
  Plus, 
  Trash2, 
  Check, 
  CheckSquare, 
  Clock, 
  ShieldAlert, 
  X,
  PlusCircle,
  TrendingUp,
  Receipt,
  UtensilsCrossed,
  User,
  LogOut,
  FolderOpen,
  ArrowRight,
  TrendingDown
} from 'lucide-react';
import { Line } from 'react-chartjs-2';
import LoginScreen from './components/LoginScreen.jsx';
import PosView from './components/PosView.jsx';
import { clearStoredAuth, getStoredAuth, hasBackendConfigured, loginWithJwt, saveStoredAuth } from './services/authService.js';
import { 
  Chart as ChartJS, 
  CategoryScale, 
  LinearScale, 
  PointElement, 
  LineElement, 
  Title, 
  Tooltip, 
  Legend, 
  Filler 
} from 'chart.js';

// Registrar componentes de Chart.js
ChartJS.register(
  CategoryScale, 
  LinearScale, 
  PointElement, 
  LineElement, 
  Title, 
  Tooltip, 
  Legend, 
  Filler
);

// --- SEED DATA INICIAL ---
const DEFAULT_USERS = [
  { id: 1, username: 'admin', nombre: 'Santiago Torres (Propietario)', rol: 'ADMIN', avatar: 'S' },
  { id: 2, username: 'cajero1', nombre: 'Jarry Vásquez (Cajero)', rol: 'CAJERO', avatar: 'J' },
  { id: 3, username: 'cocinero1', nombre: 'Danny Pezo (Chef)', rol: 'COCINA', avatar: 'D' },
  { id: 4, username: 'almacen1', nombre: 'Jheison Carranza (Almacenero)', rol: 'ALMACEN', avatar: 'J' }
];

const DEFAULT_INSUMOS = [
  { id: 1, nombre: 'Pollo Trozado (Presas)', stock_actual: 150.00, stock_minimo: 30.00, unidad_medida: 'UNIDAD' },
  { id: 2, nombre: 'Papas Peladas / Cortadas', stock_actual: 80.00, stock_minimo: 20.00, unidad_medida: 'KG' },
  { id: 3, nombre: 'Arroz Chaufa Preparado', stock_actual: 40.00, stock_minimo: 10.00, unidad_medida: 'KG' },
  { id: 4, nombre: 'Aceite Vegetal', stock_actual: 50.00, stock_minimo: 10.00, unidad_medida: 'LITRO' },
  { id: 5, nombre: 'Crema Mayonesa (Salsas)', stock_actual: 15.00, stock_minimo: 5.00, unidad_medida: 'LITRO' },
  { id: 6, nombre: 'Crema Ketchup', stock_actual: 12.00, stock_minimo: 4.00, unidad_medida: 'LITRO' },
  { id: 7, nombre: 'Gaseosa de 1.5L', stock_actual: 60.00, stock_minimo: 15.00, unidad_medida: 'UNIDAD' }
];

const DEFAULT_PRODUCTS = [
  { id: 1, nombre: '1/4 de Pollo Broaster', desc: '1 presa de pollo broaster crujiente + papas fritas + ensalada + cremas', precio: 15.00, categoria: 'individuales' },
  { id: 2, nombre: '1/2 de Pollo Broaster', desc: '2 presas de pollo broaster crujiente + doble papas fritas + ensalada + cremas', precio: 28.00, categoria: 'individuales' },
  { id: 3, nombre: 'Combo Familiar Broaster', desc: '8 presas de pollo broaster + 1kg de papas + ensalada familiar + Gaseosa 1.5L', precio: 85.00, categoria: 'combos' },
  { id: 4, nombre: 'Alitas Broaster (6 unidades)', desc: '6 alitas crujientes + papas fritas + cremas', precio: 18.00, categoria: 'individuales' },
  { id: 5, nombre: 'Chaufa con Broaster', desc: 'Arroz chaufa de la casa servido con 1 presa de pollo broaster', precio: 20.00, categoria: 'combos' }
];

const DEFAULT_RECIPES = {
  1: [ { insumoId: 1, cantidad: 1.0 }, { insumoId: 2, cantidad: 0.25 }, { insumoId: 4, cantidad: 0.1 }, { insumoId: 5, cantidad: 0.05 } ],
  2: [ { insumoId: 1, cantidad: 2.0 }, { insumoId: 2, cantidad: 0.5 }, { insumoId: 4, cantidad: 0.2 }, { insumoId: 5, cantidad: 0.08 } ],
  3: [ { insumoId: 1, cantidad: 8.0 }, { insumoId: 2, cantidad: 1.0 }, { insumoId: 4, cantidad: 0.5 }, { insumoId: 5, cantidad: 0.15 }, { insumoId: 7, cantidad: 1.0 } ],
  4: [ { insumoId: 1, cantidad: 3.0 }, { insumoId: 2, cantidad: 0.25 }, { insumoId: 4, cantidad: 0.1 } ],
  5: [ { insumoId: 1, cantidad: 1.0 }, { insumoId: 3, cantidad: 0.3 }, { insumoId: 4, cantidad: 0.05 } ]
};

const INITIAL_CAJA_HISTORY = [
  { id: 1, usuario: 'Jarry Vásquez (Cajero)', fecha_apertura: '2026-06-25 12:00', fecha_cierre: '2026-06-25 23:00', monto_apertura: 150.00, ingresos_ventas: 480.00, egresos_adicionales: 35.00, monto_cierre: 595.00, estado: 'CERRADA' }
];

const INITIAL_ORDERS = [
  { id: 101, fecha: 'Hace 45 min', cliente: 'Mesa 2', estado: 'ENTREGADO', total: 43.00, items: [{ id: 1, nombre: '1/4 de Pollo Broaster', precio: 15.00, qty: 1 }, { id: 2, nombre: '1/2 de Pollo Broaster', precio: 28.00, qty: 1 }] },
  { id: 102, fecha: 'Hace 10 min', cliente: 'Para llevar - Sr. Gomez', estado: 'EN_PREPARACION', total: 85.00, items: [{ id: 3, nombre: 'Combo Familiar Broaster', precio: 85.00, qty: 1 }] },
  { id: 103, fecha: 'Hace 2 min', cliente: 'Mesa 5', estado: 'PENDIENTE', total: 20.00, items: [{ id: 5, nombre: 'Chaufa con Broaster', precio: 20.00, qty: 1 }] }
];

const INITIAL_MOVIMIENTOS = [
  { fecha: '2026-06-26 10:15', insumo: 'Pollo Trozado (Presas)', tipo: 'ENTRADA', cantidad: 100, responsable: 'Jheison Carranza (Almacenero)', motivo: 'Compra lote proveedor avícola' },
  { fecha: '2026-06-26 14:00', insumo: 'Papas Peladas / Cortadas', tipo: 'MERMA', cantidad: 5, responsable: 'Jheison Carranza (Almacenero)', motivo: 'Papas en mal estado' }
];

const SYSTEM_MODULES = [
  {
    key: 'reports',
    title: 'Dashboard ejecutivo',
    description: 'Resumen diario de ventas, ticket promedio, alertas críticas y ranking de productos.',
    icon: BarChart3,
    target: 'reports',
    roles: ['ADMIN']
  },
  {
    key: 'pos',
    title: 'POS Ventas',
    description: 'Registro de pedidos, carrito de venta y envío inmediato a cocina.',
    icon: UtensilsCrossed,
    target: 'pos',
    roles: ['ADMIN', 'CAJERO']
  },
  {
    key: 'kitchen',
    title: 'Vista Cocina',
    description: 'Flujo de estados de pedidos desde pendientes hasta entregados.',
    icon: Flame,
    target: 'kitchen',
    roles: ['ADMIN', 'CAJERO', 'COCINA']
  },
  {
    key: 'inventory',
    title: 'Inventario',
    description: 'Stock de insumos, recetas y kardex con alertas de mínimo.',
    icon: FolderOpen,
    target: 'inventory',
    roles: ['ADMIN', 'ALMACEN']
  },
  {
    key: 'cash',
    title: 'Control de caja',
    description: 'Apertura, egresos, cierre y arqueo del turno en curso.',
    icon: Landmark,
    target: 'cash',
    roles: ['ADMIN', 'CAJERO']
  }
];

const QUICK_LOGIN_USERS = DEFAULT_USERS.map(user => ({
  key: user.username,
  demoUser: user
}));

export default function App() {
  // --- 1. ESTADOS PRINCIPALES ---
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [activeView, setActiveView] = useState('pos');
  const [currentUser, setCurrentUser] = useState(DEFAULT_USERS[0]);
  const [insumos, setInsumos] = useState([]);
  const [productos, setProductos] = useState([]);
  const [recetas, setRecetas] = useState({});
  const [pedidos, setPedidos] = useState([]);
  const [movimientos, setMovimientos] = useState([]);
  const [cajaSesiones, setCajaSesiones] = useState([]);
  const [cajaActiva, setCajaActiva] = useState(null);
  
  // Estado Login
  const [loginUser, setLoginUser] = useState('');
  const [loginPass, setLoginPass] = useState('');
  
  // Estados Auxiliares
  const [cart, setCart] = useState([]);
  const [posCategoryFilter, setPosCategoryFilter] = useState('all');
  const [inventoryTab, setInventoryTab] = useState('insumos');
  const [cartCustomerName, setCartCustomerName] = useState('Cliente General');
  
  // Modales
  const [isAperturaOpen, setIsAperturaOpen] = useState(false);
  const [isExpenseOpen, setIsExpenseOpen] = useState(false);
  const [isInvMovOpen, setIsInvMovOpen] = useState(false);
  const [isLowStockOpen, setIsLowStockOpen] = useState(false);
  const [isReceiptOpen, setIsReceiptOpen] = useState(false);
  
  // Inputs de Formularios
  const [cajaMontoInicial, setCajaMontoInicial] = useState('150.00');
  const [egresoMonto, setEgresoMonto] = useState('');
  const [egresoMotivo, setEgresoMotivo] = useState('');
  const [invMovType, setInvMovType] = useState('ENTRADA');
  const [invMovInsumoId, setInvMovInsumoId] = useState('');
  const [invMovCantidad, setInvMovCantidad] = useState('');
  const [invMovMotivo, setInvMovMotivo] = useState('');
  const [receiptData, setReceiptData] = useState(null);
  
  // Toasts
  const [toastList, setToastList] = useState([]);

  // --- 2. CARGA Y PERSISTENCIA ---
  useEffect(() => {
    const savedState = localStorage.getItem('aquicito_broaster_react_state');
    if (savedState) {
      const parsed = JSON.parse(savedState);
      setInsumos(parsed.insumos);
      setProductos(parsed.productos);
      setRecetas(parsed.recetas);
      setPedidos(parsed.pedidos);
      setMovimientos(parsed.movimientos);
      setCajaSesiones(parsed.cajaSesiones);
      
      const activa = parsed.cajaSesiones.find(s => s.estado === 'ABIERTA');
      setCajaActiva(activa || null);
    } else {
      setInsumos([...DEFAULT_INSUMOS]);
      setProductos([...DEFAULT_PRODUCTS]);
      setRecetas({...DEFAULT_RECIPES});
      setPedidos([...INITIAL_ORDERS]);
      setMovimientos([...INITIAL_MOVIMIENTOS]);
      setCajaSesiones([...INITIAL_CAJA_HISTORY]);
      setCajaActiva(null);
    }

    const storedAuth = getStoredAuth();
    if (storedAuth?.user) {
      setCurrentUser(storedAuth.user);
      setIsLoggedIn(true);
      setActiveView(storedAuth.user.rol === 'ADMIN' ? 'reports' : 'pos');
    }
  }, []);

  const saveToLocal = (updatedState) => {
    localStorage.setItem('aquicito_broaster_react_state', JSON.stringify(updatedState));
  };

  const triggerStateSave = (key, val) => {
    const currentState = {
      insumos,
      productos,
      recetas,
      pedidos,
      movimientos,
      cajaSesiones,
      ...{[key]: val}
    };
    saveToLocal(currentState);
  };

  // --- TOAST HELPER ---
  const showToast = (message, type = 'info') => {
    const id = Date.now();
    setToastList(prev => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToastList(prev => prev.filter(t => t.id !== id));
    }, 3000);
  };

  // --- RESTABLECER PROTOTIPO ---
  const handleResetPrototype = () => {
    if (window.confirm("¿Estás seguro de restablecer todos los datos del prototipo? Perderás todas las ventas de la sesión.")) {
      localStorage.removeItem('aquicito_broaster_react_state');
      setInsumos([...DEFAULT_INSUMOS]);
      setProductos([...DEFAULT_PRODUCTS]);
      setRecetas({...DEFAULT_RECIPES});
      setPedidos([...INITIAL_ORDERS]);
      setMovimientos([...INITIAL_MOVIMIENTOS]);
      setCajaSesiones([...INITIAL_CAJA_HISTORY]);
      setCajaActiva(null);
      setCart([]);
      setActiveView('pos');
      showToast("Prototipo reiniciado a valores iniciales", "success");
    }
  };

  // --- INICIAR SESIÓN ---
  const handleLoginSubmit = (e) => {
    if (e) e.preventDefault();
    if (!loginUser.trim()) {
      showToast("Ingrese un nombre de usuario", "danger");
      return;
    }

    const username = loginUser.trim().toLowerCase();

    const completeLogin = (user, authToken) => {
      setCurrentUser(user);
      setIsLoggedIn(true);
      setLoginUser('');
      setLoginPass('');
      setActiveView(user.rol === 'ADMIN' ? 'reports' : 'pos');
      saveStoredAuth({ token: authToken || null, user });
      showToast(`¡Bienvenido, ${user.nombre}!`, "success");
    };

    if (hasBackendConfigured()) {
      loginWithJwt(username, loginPass)
        .then((response) => {
          const matchedUser = DEFAULT_USERS.find(u => u.username === (response.username || username)) || {
            id: Date.now(),
            username: response.username || username,
            nombre: response.nombre || username,
            rol: response.rol || 'CAJERO',
            avatar: (response.nombre || username).charAt(0).toUpperCase()
          };

          completeLogin(matchedUser, response.token);
        })
        .catch((error) => {
          showToast(error.message || 'No se pudo autenticar con el backend', 'danger');
        });
      return;
    }

    const foundUser = DEFAULT_USERS.find(u => u.username === username) ||
      DEFAULT_USERS.find(u => u.rol.toLowerCase() === username);

    if (foundUser) {
      completeLogin(foundUser, null);
    } else {
      showToast("Usuario no encontrado (Prueba con: admin, cajero1, cocinero1)", "danger");
    }
  };

  const handleQuickLogin = (userObj) => {
    setCurrentUser(userObj);
    setIsLoggedIn(true);
    setActiveView(userObj.rol === 'ADMIN' ? 'reports' : 'pos');
    saveStoredAuth({ token: null, user: userObj });
    showToast(`¡Sesión iniciada como ${userObj.nombre}!`, "success");
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    setCart([]);
    clearStoredAuth();
    showToast("Sesión cerrada", "info");
  };

  // --- CAMBIO DE ROL DIRECTO ---
  const handleRoleChange = (roleName) => {
    const user = DEFAULT_USERS.find(u => u.rol === roleName);
    if (user) {
      setCurrentUser(user);
      showToast(`Sesión cambiada a: ${user.nombre}`, "info");
    }
  };

  // --- CONTROL DE ACCESOS ---
  const hasAccess = (view) => {
    const role = currentUser.rol;
    if (view === 'inventory') return ['ADMIN', 'ALMACEN'].includes(role);
    if (view === 'cash') return ['ADMIN', 'CAJERO'].includes(role);
    if (view === 'reports') return ['ADMIN'].includes(role);
    return true;
  };

  // --- LÓGICA DE PUNTO DE VENTA (POS) ---
  const addToCart = (productId) => {
    if (!cajaActiva) {
      showToast("Debe iniciar un turno de caja antes de registrar pedidos.", "warning");
      setActiveView('cash');
      return;
    }

    const prod = productos.find(p => p.id === productId);
    const existing = cart.find(item => item.id === productId);

    if (existing) {
      setCart(cart.map(item => item.id === productId ? { ...item, qty: item.qty + 1 } : item));
    } else {
      setCart([...cart, { id: prod.id, nombre: prod.nombre, precio: prod.precio, qty: 1 }]);
    }
    showToast(`Añadido al carrito: ${prod.nombre}`, "success");
  };

  const updateCartQty = (productId, amount) => {
    const updated = cart.map(item => {
      if (item.id === productId) {
        const newQty = item.qty + amount;
        return newQty > 0 ? { ...item, qty: newQty } : null;
      }
      return item;
    }).filter(Boolean);
    setCart(updated);
  };

  const getCartTotal = () => cart.reduce((sum, item) => sum + (item.precio * item.qty), 0);

  const handleRegisterOrder = () => {
    if (cart.length === 0) {
      showToast("El carrito está vacío", "danger");
      return;
    }

    const total = getCartTotal();
    const newOrder = {
      id: pedidos.length > 0 ? Math.max(...pedidos.map(o => o.id)) + 1 : 101,
      fecha: 'Ahora',
      cliente: cartCustomerName,
      estado: 'PENDIENTE',
      total: total,
      items: [...cart]
    };

    const updatedPedidos = [...pedidos, newOrder];
    setPedidos(updatedPedidos);

    // Sumar a la caja activa
    const updatedCajaHistory = cajaSesiones.map(s => {
      if (s.id === cajaActiva.id) {
        const u = { ...s, ingresos_ventas: s.ingresos_ventas + total };
        setCajaActiva(u);
        return u;
      }
      return s;
    });

    setCajaSesiones(updatedCajaHistory);
    setCart([]);
    setCartCustomerName('Cliente General');
    
    // Guardar
    const currentState = {
      insumos, productos, recetas,
      pedidos: updatedPedidos,
      movimientos,
      cajaSesiones: updatedCajaHistory
    };
    saveToLocal(currentState);

    showToast(`¡Pedido #${newOrder.id} enviado a Cocina!`, "success");
  };

  // --- LÓGICA DE COCINA ---
  const handleOrderStatusChange = (orderId, nextState) => {
    const order = pedidos.find(o => o.id === orderId);
    if (!order) return;

    if (nextState === 'ENTREGADO') {
      // Validar y descontar stock
      const stockCheck = checkAndDeductStock(order);
      if (!stockCheck.success) {
        showToast(stockCheck.message, "danger");
        return;
      }
    }

    const updatedPedidos = pedidos.map(o => o.id === orderId ? { ...o, estado: nextState } : o);
    setPedidos(updatedPedidos);
    triggerStateSave('pedidos', updatedPedidos);
    showToast(`Pedido #${orderId} actualizado a ${nextState}`, "info");
  };

  const checkAndDeductStock = (order) => {
    // Clona los insumos para comprobar
    const insumosTemp = [...insumos];
    const newMovs = [];

    // Comprobar si hay insumos suficientes
    for (const item of order.items) {
      const recipeIngredients = recetas[item.id];
      if (recipeIngredients) {
        for (const ingredient of recipeIngredients) {
          const required = ingredient.cantidad * item.qty;
          const targetInsumo = insumosTemp.find(i => i.id === ingredient.insumoId);
          
          if (!targetInsumo || targetInsumo.stock_actual < required) {
            const name = targetInsumo ? targetInsumo.nombre : 'Desconocido';
            return {
              success: false,
              message: `Falta de Stock: Se requiere ${required.toFixed(2)} ${targetInsumo?.unidad_medida || ''} de "${name}"`
            };
          }

          // Descontar temporalmente
          targetInsumo.stock_actual -= required;
          newMovs.push({
            fecha: getNowString(),
            insumo: targetInsumo.nombre,
            tipo: 'SALIDA',
            cantidad: required,
            responsable: currentUser.nombre,
            motivo: `Descuento automático por Venta Pedido #${order.id}`
          });
        }
      }
    }

    // Si todo es correcto, aplicar cambio real
    setInsumos(insumosTemp);
    const updatedMovs = [...movimientos, ...newMovs];
    setMovimientos(updatedMovs);

    const currentState = {
      productos, recetas, pedidos,
      insumos: insumosTemp,
      movimientos: updatedMovs,
      cajaSesiones
    };
    saveToLocal(currentState);

    return { success: true };
  };

  // --- LÓGICA DE ALMACÉN ---
  const handleOpenAddStock = (type) => {
    setInvMovType(type);
    if (insumos.length > 0) {
      setInvMovInsumoId(insumos[0].id.toString());
    }
    setInvMovCantidad('');
    setInvMovMotivo('');
    setIsInvMovOpen(true);
  };

  const handleRegisterInvMovement = () => {
    const id = parseInt(invMovInsumoId);
    const qty = parseFloat(invMovCantidad);
    if (isNaN(qty) || qty <= 0) {
      showToast("Cantidad incorrecta", "danger");
      return;
    }

    const targetInsumo = insumos.find(i => i.id === id);
    if (invMovType === 'MERMA' && targetInsumo.stock_actual < qty) {
      showToast("No puede reportar más mermas del stock real", "danger");
      return;
    }

    const updatedInsumos = insumos.map(i => {
      if (i.id === id) {
        return {
          ...i,
          stock_actual: invMovType === 'ENTRADA' ? i.stock_actual + qty : i.stock_actual - qty
        };
      }
      return i;
    });

    const newMov = {
      fecha: getNowString(),
      insumo: targetInsumo.nombre,
      tipo: invMovType,
      cantidad: qty,
      responsable: currentUser.nombre,
      motivo: invMovMotivo || (invMovType === 'ENTRADA' ? 'Ingreso de mercadería' : 'Merma de cocina')
    };

    const updatedMovs = [...movimientos, newMov];
    setInsumos(updatedInsumos);
    setMovimientos(updatedMovs);
    
    const currentState = {
      productos, recetas, pedidos, cajaSesiones,
      insumos: updatedInsumos,
      movimientos: updatedMovs
    };
    saveToLocal(currentState);

    setIsInvMovOpen(false);
    showToast("Movimiento de inventario guardado correctamente", "success");
  };

  // --- LÓGICA DE CAJA ---
  const handleExecuteApertura = () => {
    const initialAmt = parseFloat(cajaMontoInicial);
    if (isNaN(initialAmt) || initialAmt < 0) {
      showToast("Monto inicial no válido", "danger");
      return;
    }

    const newSession = {
      id: cajaSesiones.length + 1,
      usuario: currentUser.nombre,
      fecha_apertura: getNowString(),
      fecha_cierre: null,
      monto_apertura: initialAmt,
      ingresos_ventas: 0.0,
      egresos_adicionales: 0.0,
      monto_cierre: null,
      estado: 'ABIERTA'
    };

    const updatedSessions = [...cajaSesiones, newSession];
    setCajaSesiones(updatedSessions);
    setCajaActiva(newSession);
    triggerStateSave('cajaSesiones', updatedSessions);

    setIsAperturaOpen(false);
    showToast("Caja abierta. Turno de ventas iniciado.", "success");
  };

  const handleRegisterExpense = () => {
    const amt = parseFloat(egresoMonto);
    if (isNaN(amt) || amt <= 0 || !egresoMotivo) {
      showToast("Faltan ingresar datos o el monto es incorrecto", "danger");
      return;
    }

    const currentBalance = cajaActiva.monto_apertura + cajaActiva.ingresos_ventas - cajaActiva.egresos_adicionales;
    if (amt > currentBalance) {
      showToast("No cuenta con suficiente efectivo en caja", "danger");
      return;
    }

    const updatedSessions = cajaSesiones.map(s => {
      if (s.id === cajaActiva.id) {
        const u = { ...s, egresos_adicionales: s.egresos_adicionales + amt };
        setCajaActiva(u);
        return u;
      }
      return s;
    });

    setCajaSesiones(updatedSessions);
    triggerStateSave('cajaSesiones', updatedSessions);
    setIsExpenseOpen(false);
    setEgresoMonto('');
    setEgresoMotivo('');
    showToast("Egreso registrado con éxito", "warning");
  };

  const handleCloseCaja = () => {
    const finalBalance = cajaActiva.monto_apertura + cajaActiva.ingresos_ventas - cajaActiva.egresos_adicionales;
    
    if (window.confirm(`¿Confirmar cierre de caja?\nSaldo Neto a entregar: S/. ${finalBalance.toFixed(2)}`)) {
      const closedSession = {
        ...cajaActiva,
        monto_cierre: finalBalance,
        fecha_cierre: getNowString(),
        estado: 'CERRADA'
      };

      const updatedSessions = cajaSesiones.map(s => s.id === cajaActiva.id ? closedSession : s);
      setCajaSesiones(updatedSessions);
      setReceiptData(closedSession);
      setIsReceiptOpen(true);
      setCajaActiva(null);
      triggerStateSave('cajaSesiones', updatedSessions);
      showToast("Caja cerrada y arqueada con éxito.", "success");
    }
  };

  // --- LÓGICA DE REPORTES ---
  const getVentasTotales = () => pedidos.reduce((sum, o) => o.estado !== 'CANCELADO' ? sum + o.total : sum, 0);
  const getMermasCount = () => movimientos.filter(m => m.tipo === 'MERMA').length;
  const getTicketPromedio = () => pedidos.length > 0 ? getVentasTotales() / pedidos.length : 0;
  
  const getTopProductsData = () => {
    const rankMap = {};
    productos.forEach(p => rankMap[p.nombre] = { qty: 0, revenue: 0 });
    pedidos.forEach(p => {
      p.items.forEach(item => {
        if (rankMap[item.nombre]) {
          rankMap[item.nombre].qty += item.qty;
          rankMap[item.nombre].revenue += (item.precio * item.qty);
        }
      });
    });
    return Object.entries(rankMap).sort((a,b) => b[1].qty - a[1].qty);
  };

  const getChartData = () => {
    const labels = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Hoy'];
    const dataSales = [410, 390, 520, 480, 680, getVentasTotales()];
    return {
      labels,
      datasets: [
        {
          label: 'Ventas Diarias (S/.)',
          data: dataSales,
          borderColor: '#FF5722',
          backgroundColor: 'rgba(255, 87, 34, 0.1)',
          borderWidth: 3,
          fill: true,
          tension: 0.3
        }
      ]
    };
  };

  // --- CRITICAL ALERT COUNTER ---
  const getLowStockCount = () => insumos.filter(i => i.stock_actual <= i.stock_minimo).length;

  // --- HELPERS AUX ---
  const getNowString = () => {
    const now = new Date();
    return `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`;
  };

  // --- RENDERIZADO CONDICIONAL: PANTALLA DE LOGIN ---
  if (!isLoggedIn) {
    return (
      <LoginScreen
        loginUser={loginUser}
        loginPass={loginPass}
        setLoginUser={setLoginUser}
        setLoginPass={setLoginPass}
        onSubmit={handleLoginSubmit}
        onQuickLogin={handleQuickLogin}
        toastList={toastList}
        previewModules={SYSTEM_MODULES}
        quickUsers={DEFAULT_USERS}
        backendConfigured={hasBackendConfigured()}
      />
    );
  }

  // --- RENDERIZADO CONDICIONAL: PANTALLA PRINCIPAL CON APP LAYOUT ---
  return (
    <div class="app-container">
      {/* SIDEBAR */}
      <aside>
        <div>
          <div class="sidebar-brand">
            <Drumstick size={28} />
            <h1>Aquicito Broaster</h1>
          </div>
          <ul class="sidebar-menu">
            <li class={activeView === 'reports' ? 'active' : ''}>
              <button onClick={() => setActiveView('reports')}><BarChart3 size={16} /> Dashboard</button>
            </li>
            <li class={activeView === 'pos' ? 'active' : ''}>
              <button onClick={() => setActiveView('pos')}><UtensilsCrossed size={16} /> POS Ventas</button>

            <div className="login-module-preview">
              {SYSTEM_MODULES.map(module => {
                const ModuleIcon = module.icon;
                return (
                  <div key={module.key} className="login-module-chip">
                    <ModuleIcon size={16} />
                    <div>
                      <strong>{module.title}</strong>
                      <span>Acceso por rol: {module.roles.join(' / ')}</span>
                    </div>
                  </div>
                );
              })}
            </div>

            </li>
            <li class={activeView === 'kitchen' ? 'active' : ''}>
              <button onClick={() => setActiveView('kitchen')}><Flame size={16} /> Vista Cocina</button>
            </li>
            <li class={activeView === 'inventory' ? 'active' : ''}>
              <button onClick={() => setActiveView('inventory')}><FolderOpen size={16} /> Inventario</button>
            </li>
            <li class={activeView === 'cash' ? 'active' : ''}>
              <button onClick={() => setActiveView('cash')}><Landmark size={16} /> Control Caja</button>
            </li>
          </ul>
        </div>
        <div>
          <ul class="sidebar-menu" style={{ paddingTop: 0 }}>
            <li>
              <button onClick={handleResetPrototype} style={{ color: 'var(--text-muted)' }}><RotateCcw size={16} /> Restablecer</button>
            </li>
            <li>
              <button onClick={handleLogout} style={{ color: 'var(--danger)' }}><LogOut size={16} /> Cerrar Sesión</button>
            </li>
          </ul>
          <div class="sidebar-user">
            <div class="sidebar-user-avatar">{currentUser.avatar}</div>
            <div class="sidebar-user-info">
              <div class="sidebar-user-name">{currentUser.nombre}</div>
              <div class="sidebar-user-role">
                <span class={`badge-role role-${currentUser.rol.toLowerCase()}`}>{currentUser.rol}</span>
              </div>
            </div>
          </div>
        </div>
      </aside>

      {/* MAIN CONTAINER */}
      <main>
        <header>
          <div class="header-title-section">
            <h2>
              {activeView === 'reports' && 'Dashboard / Reportes'}
              {activeView === 'pos' && 'Punto de Venta (POS)'}
              {activeView === 'kitchen' && 'Monitoreo de Cocina'}
              {activeView === 'inventory' && 'Gestión de Inventario y Recetas'}
              {activeView === 'cash' && 'Arqueo y Control de Caja'}
            </h2>
          </div>
          
          <div class="header-controls">
            <div class="role-switcher-container">
              <label>Ver como:</label>
              <select value={currentUser.rol} onChange={(e) => handleRoleChange(e.target.value)}>
                <option value="ADMIN">Administrador</option>
                <option value="CAJERO">Cajero</option>
                <option value="COCINA">Cocinero</option>
                <option value="ALMACEN">Almacenero</option>
              </select>
            </div>

            <div class="alert-bell" onClick={() => setIsLowStockOpen(true)}>
              <Bell size={20} />
              {getLowStockCount() > 0 && <span class="alert-badge">{getLowStockCount()}</span>}
            </div>
          </div>
        </header>

        <div class="content-area">
          {/* VIEW: RESTRICTED PAGE */}
          {!hasAccess(activeView) ? (
            <div class="access-restricted-card">
              <ShieldAlert />
              <h3>Acceso Restringido</h3>
              <p>Su rol actual ({currentUser.rol}) no tiene los permisos suficientes para acceder a este módulo financiero/logístico.</p>
              <button class="btn btn-secondary" onClick={() => setActiveView('pos')}>Volver al POS</button>
            </div>
          ) : (
            <>
              {/* VIEW: REPORTS (ADMIN DASHBOARD) */}
              {activeView === 'reports' && (
                <div>
                  <div className="module-hub-section">
                    <div className="section-heading">
                      <div>
                        <span className="section-kicker">Módulos del sistema</span>
                        <h3>Navegación rápida por áreas operativas</h3>
                      </div>
                    </div>
                    <div className="module-hub-grid">
                      {SYSTEM_MODULES.map(module => {
                        const ModuleIcon = module.icon;
                        const isActiveModule = activeView === module.target;
                        const accessText = module.roles.length === 1 ? module.roles[0] : module.roles.join(' · ');

                        return (
                          <button
                            key={module.key}
                            type="button"
                            className={`module-hub-card ${isActiveModule ? 'active' : ''}`}
                            onClick={() => setActiveView(module.target)}
                          >
                            <div className="module-hub-icon">
                              <ModuleIcon size={18} />
                            </div>
                            <div className="module-hub-copy">
                              <strong>{module.title}</strong>
                              <p>{module.description}</p>
                            </div>
                            <span className="module-hub-meta">{accessText}</span>
                          </button>
                        );
                      })}
                    </div>
                  </div>

                  <div class="reports-grid-kpi">
                    <div class="kpi-card">
                      <div class="kpi-icon"><TrendingUp /></div>
                      <div class="kpi-data">
                        <div class="kpi-value">S/. {getVentasTotales().toFixed(2)}</div>
                        <div class="kpi-label">Ventas Totales Hoy</div>
                      </div>
                    </div>
                    
                    <div class="kpi-card">
                      <div class="kpi-icon" style={{ color: 'var(--warning)', backgroundColor: 'rgba(245, 158, 11, 0.1)' }}><UtensilsCrossed /></div>
                      <div class="kpi-data">
                        <div class="kpi-value">{pedidos.length}</div>
                        <div class="kpi-label">Pedidos Registrados</div>
                      </div>
                    </div>

                    <div class="kpi-card">
                      <div class="kpi-icon" style={{ color: 'var(--danger)', backgroundColor: 'rgba(239, 68, 68, 0.1)' }}><TrendingDown /></div>
                      <div class="kpi-data">
                        <div class="kpi-value">{getMermasCount()}</div>
                        <div class="kpi-label">Mermas de Alimentos</div>
                      </div>
                    </div>

                    <div class="kpi-card">
                      <div class="kpi-icon" style={{ color: 'var(--success)', backgroundColor: 'rgba(16, 185, 129, 0.1)' }}><Receipt /></div>
                      <div class="kpi-data">
                        <div class="kpi-value">S/. {getTicketPromedio().toFixed(2)}</div>
                        <div class="kpi-label">Ticket de Venta Promedio</div>
                      </div>
                    </div>
                  </div>

                  {/* ALERTA DIRECTA DE STOCK BAJO EN EL DASHBOARD */}
                  {getLowStockCount() > 0 && (
                    <div style={{
                      backgroundColor: 'rgba(239, 68, 68, 0.15)',
                      border: '1px solid rgba(239, 68, 68, 0.4)',
                      borderRadius: '12px',
                      padding: '16px',
                      marginBottom: '24px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between'
                    }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                        <AlertTriangle color="var(--danger)" size={24} />
                        <div>
                          <strong style={{ color: 'white', fontSize: '15px' }}>¡Atención! Hay insumos con stock crítico</strong>
                          <p style={{ color: 'var(--text-muted)', fontSize: '12px', marginTop: '2px' }}>
                            Tienes {getLowStockCount()} ingredientes por debajo del límite mínimo. Esto puede afectar la preparación de los platos.
                          </p>
                        </div>
                      </div>
                      <button class="btn btn-danger" style={{ padding: '8px 16px', fontSize: '13px' }} onClick={() => setIsLowStockOpen(true)}>
                        Ver Alertas
                      </button>
                    </div>
                  )}

                  <div class="reports-layout">
                    <div class="chart-container-card">
                      <h3>Tendencia de Ventas Diarias</h3>
                      <div class="chart-wrapper">
                        <Line data={getChartData()} options={{ responsive: true, maintainAspectRatio: false }} />
                      </div>
                    </div>

                    <div class="chart-container-card">
                      <h3>Platos Más Vendidos</h3>
                      <div class="table-responsive" style={{ border: 'none' }}>
                        <table>
                          <thead>
                            <tr>
                              <th>Producto</th>
                              <th style={{ textAlign: 'right' }}>Vendidos</th>
                              <th style={{ textAlign: 'right' }}>Recaudado</th>
                            </tr>
                          </thead>
                          <tbody>
                            {getTopProductsData().map(([name, data]) => (
                              <tr key={name}>
                                <td style={{ fontWeight: '600' }}>{name}</td>
                                <td style={{ textAlign: 'right', color: 'var(--accent-light)', fontWeight: '700' }}>{data.qty} u.</td>
                                <td style={{ textAlign: 'right' }}>S/. {data.revenue.toFixed(2)}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>

                  {/* PANEL DE ACCIONES RÁPIDAS */}
                  <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                    <button class="btn" onClick={() => setActiveView('pos')}><UtensilsCrossed size={16} /> Tomar Pedido (POS)</button>
                    <button class="btn btn-secondary" onClick={() => setActiveView('cash')}><Landmark size={16} /> Control de Caja</button>
                    <button class="btn btn-secondary" onClick={() => setActiveView('inventory')}><FolderOpen size={16} /> Gestionar Almacén</button>
                  </div>
                </div>
              )}

              {/* VIEW: POS */}
              {activeView === 'pos' && (
                <PosView
                  productos={productos}
                  cart={cart}
                  cartCustomerName={cartCustomerName}
                  posCategoryFilter={posCategoryFilter}
                  setPosCategoryFilter={setPosCategoryFilter}
                  setCartCustomerName={setCartCustomerName}
                  addToCart={addToCart}
                  updateCartQty={updateCartQty}
                  clearCart={() => setCart([])}
                  getCartTotal={getCartTotal}
                  handleRegisterOrder={handleRegisterOrder}
                />
              )}

              {/* VIEW: KITCHEN */}
              {activeView === 'kitchen' && (
                <div class="kitchen-columns">
                  {/* Pendientes */}
                  <div class="kitchen-col">
                    <div class="kitchen-col-header">
                      <h3><Clock size={16} style={{ color: 'var(--danger)' }} /> Pendientes</h3>
                      <span class="kitchen-order-count">{pedidos.filter(o => o.estado === 'PENDIENTE').length}</span>
                    </div>
                    <div class="kitchen-order-list">
                      {pedidos.filter(o => o.estado === 'PENDIENTE').map(order => (
                        <div key={order.id} class="kitchen-card">
                          <div class="kitchen-card-header">
                            <span>Orden #{order.id}</span>
                            <span>{order.fecha}</span>
                          </div>
                          <div class="kitchen-card-customer">{order.cliente}</div>
                          <ul class="kitchen-card-items">
                            {order.items.map(item => (
                              <li key={item.id} class="kitchen-card-item">
                                <span>{item.nombre}</span>
                                <span class="kitchen-card-item-qty">x{item.qty}</span>
                              </li>
                            ))}
                          </ul>
                          <div class="kitchen-card-footer">
                            <button class="btn btn-secondary" style={{ padding: '6px 12px', fontSize: '12px' }} onClick={() => handleOrderStatusChange(order.id, 'EN_PREPARACION')}>
                              <Flame size={12} /> Preparar
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Cocinando */}
                  <div class="kitchen-col">
                    <div class="kitchen-col-header">
                      <h3><Flame size={16} style={{ color: 'var(--warning)' }} /> Cocinando</h3>
                      <span class="kitchen-order-count">{pedidos.filter(o => o.estado === 'EN_PREPARACION').length}</span>
                    </div>
                    <div class="kitchen-order-list">
                      {pedidos.filter(o => o.estado === 'EN_PREPARACION').map(order => (
                        <div key={order.id} class="kitchen-card">
                          <div class="kitchen-card-header">
                            <span>Orden #{order.id}</span>
                            <span>{order.fecha}</span>
                          </div>
                          <div class="kitchen-card-customer">{order.cliente}</div>
                          <ul class="kitchen-card-items">
                            {order.items.map(item => (
                              <li key={item.id} class="kitchen-card-item">
                                <span>{item.nombre}</span>
                                <span class="kitchen-card-item-qty">x{item.qty}</span>
                              </li>
                            ))}
                          </ul>
                          <div class="kitchen-card-footer">
                            <button class="btn btn-success" style={{ padding: '6px 12px', fontSize: '12px' }} onClick={() => handleOrderStatusChange(order.id, 'ENTREGADO')}>
                              <Check size={12} /> Entregar
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Entregado */}
                  <div class="kitchen-col">
                    <div class="kitchen-col-header">
                      <h3><CheckSquare size={16} style={{ color: 'var(--success)' }} /> Entregados</h3>
                      <span class="kitchen-order-count">{pedidos.filter(o => o.estado === 'ENTREGADO').length}</span>
                    </div>
                    <div class="kitchen-order-list">
                      {pedidos.filter(o => o.estado === 'ENTREGADO').map(order => (
                        <div key={order.id} class="kitchen-card">
                          <div class="kitchen-card-header">
                            <span>Orden #{order.id}</span>
                            <span>{order.fecha}</span>
                          </div>
                          <div class="kitchen-card-customer">{order.cliente}</div>
                          <ul class="kitchen-card-items">
                            {order.items.map(item => (
                              <li key={item.id} class="kitchen-card-item">
                                <span>{item.nombre}</span>
                                <span class="kitchen-card-item-qty">x{item.qty}</span>
                              </li>
                            ))}
                          </ul>
                          <div class="kitchen-card-footer">
                            <span style={{ color: 'var(--success)', fontWeight: '700', fontSize: '12px' }}><Check size={14} /> Listo</span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              )}

              {/* VIEW: INVENTORY */}
              {activeView === 'inventory' && (
                <div>
                  <div class="tabs-header">
                    <button class={`tab-btn ${inventoryTab === 'insumos' ? 'active' : ''}`} onClick={() => setInventoryTab('insumos')}>Insumos</button>
                    <button class={`tab-btn ${inventoryTab === 'recetas' ? 'active' : ''}`} onClick={() => setInventoryTab('recetas')}>Recetas / Fórmulas</button>
                    <button class={`tab-btn ${inventoryTab === 'movimientos' ? 'active' : ''}`} onClick={() => setInventoryTab('movimientos')}>Kardex / Historial</button>
                  </div>

                  {inventoryTab === 'insumos' && (
                    <div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '16px' }}>
                        <h3>Stock de Ingredientes</h3>
                        <div class="caja-actions-panel">
                          <button class="btn btn-secondary" onClick={() => handleOpenAddStock('ENTRADA')}><Plus size={14} /> Registrar Compra</button>
                          <button class="btn btn-danger" onClick={() => handleOpenAddStock('MERMA')}><Trash2 size={14} /> Registrar Merma</button>
                        </div>
                      </div>

                      <div class="table-responsive">
                        <table>
                          <thead>
                            <tr>
                              <th>Insumo</th>
                              <th>Mínimo</th>
                              <th>Stock Actual</th>
                              <th>Porcentaje</th>
                              <th>Estado</th>
                              <th>Unidad</th>
                            </tr>
                          </thead>
                          <tbody>
                            {insumos.map(ins => {
                              const ref = ins.stock_minimo * 3.5;
                              let percent = (ins.stock_actual / ref) * 100;
                              if (percent > 100) percent = 100;

                              let barColor = 'fill-success';
                              let badgeColor = { bg: 'rgba(16,185,129,0.2)', text: 'var(--success)', label: 'Normal' };

                              if (ins.stock_actual <= ins.stock_minimo) {
                                barColor = 'fill-danger';
                                badgeColor = { bg: 'rgba(239,68,68,0.2)', text: 'var(--danger)', label: 'CRÍTICO' };
                              } else if (ins.stock_actual <= (ins.stock_minimo * 1.5)) {
                                barColor = 'fill-warning';
                                badgeColor = { bg: 'rgba(245,158,11,0.2)', text: 'var(--warning)', label: 'Alerta' };
                              }

                              return (
                                <tr key={ins.id}>
                                  <td style={{ fontWeight: '600' }}>{ins.nombre}</td>
                                  <td>{ins.stock_minimo}</td>
                                  <td style={{ fontWeight: '700' }}>{ins.stock_actual.toFixed(2)}</td>
                                  <td>
                                    <div class="stock-progress-container">
                                      <div class="stock-progress-text"><span>{Math.round(percent)}%</span></div>
                                      <div class="stock-progress-bar-bg">
                                        <div class={`stock-progress-bar-fill ${barColor}`} style={{ width: `${percent}%` }}></div>
                                      </div>
                                    </div>
                                  </td>
                                  <td><span class="badge-status" style={{ backgroundColor: badgeColor.bg, color: badgeColor.text }}>{badgeColor.label}</span></td>
                                  <td>{ins.unidad_medida}</td>
                                </tr>
                              );
                            })}
                          </tbody>
                        </table>
                      </div>
                    </div>
                  )}

                  {inventoryTab === 'recetas' && (
                    <div class="table-responsive">
                      <table>
                        <thead>
                          <tr>
                            <th>Producto</th>
                            <th>Ingrediente</th>
                            <th>Descuento Fijo</th>
                            <th>Unidad</th>
                          </tr>
                        </thead>
                        <tbody>
                          {productos.map(p => {
                            const recipe = recetas[p.id] || [];
                            return recipe.map((ing, idx) => {
                              const itemInsumo = insumos.find(i => i.id === ing.insumoId);
                              return (
                                <tr key={`${p.id}-${ing.insumoId}`}>
                                  {idx === 0 && (
                                    <td rowSpan={recipe.length} style={{ fontWeight: '600', verticalAlign: 'top' }}>{p.nombre}</td>
                                  )}
                                  <td>{itemInsumo?.nombre}</td>
                                  <td style={{ color: 'var(--accent-light)', fontWeight: '700' }}>{ing.cantidad.toFixed(3)}</td>
                                  <td>{itemInsumo?.unidad_medida}</td>
                                </tr>
                              );
                            });
                          })}
                        </tbody>
                      </table>
                    </div>
                  )}

                  {inventoryTab === 'movimientos' && (
                    <div class="table-responsive">
                      <table>
                        <thead>
                          <tr>
                            <th>Fecha</th>
                            <th>Insumo</th>
                            <th>Tipo</th>
                            <th>Cantidad</th>
                            <th>Responsable</th>
                            <th>Detalle</th>
                          </tr>
                        </thead>
                        <tbody>
                          {movimientos.slice().reverse().map((m, idx) => (
                            <tr key={idx}>
                              <td>{m.fecha}</td>
                              <td style={{ fontWeight: '600' }}>{m.insumo}</td>
                              <td>
                                <span class="badge-status" style={{ 
                                  backgroundColor: m.tipo === 'ENTRADA' ? 'rgba(16,185,129,0.2)' : m.tipo === 'SALIDA' ? 'rgba(59,130,246,0.2)' : 'rgba(239,68,68,0.2)',
                                  color: m.tipo === 'ENTRADA' ? 'var(--success)' : m.tipo === 'SALIDA' ? '#93C5FD' : 'var(--danger)'
                                }}>
                                  {m.tipo}
                                </span>
                              </td>
                              <td style={{ fontWeight: '700' }}>{m.cantidad.toFixed(2)}</td>
                              <td>{m.responsable}</td>
                              <td style={{ color: 'var(--text-muted)' }}>{m.motivo}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              )}

              {/* VIEW: CASH */}
              {activeView === 'cash' && (
                <div>
                  <div class="caja-summary-card">
                    <div class="caja-status-banner">
                      <div class="caja-status-title">
                        <span class={`caja-indicator ${cajaActiva ? 'open' : 'closed'}`}></span>
                        <span>Caja: <strong>{cajaActiva ? 'ABIERTA' : 'CERRADA'}</strong></span>
                      </div>
                      <div>
                        {!cajaActiva ? (
                          <button class="btn btn-success" onClick={() => setIsAperturaOpen(true)}><Plus size={14} /> Aperturar Caja</button>
                        ) : (
                          <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Operador: <strong>{cajaActiva.usuario}</strong></span>
                        )}
                      </div>
                    </div>

                    <div class="caja-grid">
                      <div class="caja-metric-card">
                        <div class="caja-metric-label">Fondo Inicial</div>
                        <div class="caja-metric-value">S/. {cajaActiva ? cajaActiva.monto_apertura.toFixed(2) : '0.00'}</div>
                      </div>
                      <div class="caja-metric-card">
                        <div class="caja-metric-label">Ventas del Turno</div>
                        <div class="caja-metric-value" style={{ color: 'var(--success)' }}>S/. {cajaActiva ? cajaActiva.ingresos_ventas.toFixed(2) : '0.00'}</div>
                      </div>
                      <div class="caja-metric-card">
                        <div class="caja-metric-label">Egresos de Turno</div>
                        <div class="caja-metric-value" style={{ color: 'var(--danger)' }}>S/. {cajaActiva ? cajaActiva.egresos_adicionales.toFixed(2) : '0.00'}</div>
                      </div>
                      <div class="caja-metric-card">
                        <div class="caja-metric-label">Saldo Neto en Caja</div>
                        <div class="caja-metric-value" style={{ color: 'var(--accent-light)' }}>
                          S/. {cajaActiva ? (cajaActiva.monto_apertura + cajaActiva.ingresos_ventas - cajaActiva.egresos_adicionales).toFixed(2) : '0.00'}
                        </div>
                      </div>
                    </div>

                    {cajaActiva && (
                      <div class="caja-actions-panel">
                        <button class="btn btn-secondary" onClick={() => setIsExpenseOpen(true)}><PlusCircle size={14} /> Registrar Egreso Manual</button>
                        <button class="btn btn-danger" onClick={handleCloseCaja}><X size={14} /> Cerrar Caja y Arqueo</button>
                      </div>
                    )}
                  </div>

                  <h3 style={{ marginBottom: '12px' }}>Historial de Turnos de Caja</h3>
                  <div class="table-responsive">
                    <table>
                      <thead>
                        <tr>
                          <th>Usuario</th>
                          <th>Apertura</th>
                          <th>Cierre</th>
                          <th>Monto Inicial</th>
                          <th>Ventas</th>
                          <th>Gastos</th>
                          <th>Cuadre Final</th>
                          <th>Estado</th>
                        </tr>
                      </thead>
                      <tbody>
                        {cajaSesiones.slice().reverse().map(s => (
                          <tr key={s.id}>
                            <td style={{ fontWeight: '600' }}>{s.usuario}</td>
                            <td>{s.fecha_apertura}</td>
                            <td>{s.fecha_cierre || 'En Curso'}</td>
                            <td>S/. {s.monto_apertura.toFixed(2)}</td>
                            <td style={{ color: 'var(--success)' }}>S/. {s.ingresos_ventas.toFixed(2)}</td>
                            <td style={{ color: 'var(--danger)' }}>S/. {s.egresos_adicionales.toFixed(2)}</td>
                            <td style={{ fontWeight: '700' }}>{s.monto_cierre ? `S/. ${s.monto_cierre.toFixed(2)}` : '-'}</td>
                            <td>
                              <span class="badge-status" style={{ 
                                backgroundColor: s.estado === 'ABIERTA' ? 'rgba(16,185,129,0.2)' : 'rgba(71,85,105,0.2)',
                                color: s.estado === 'ABIERTA' ? 'var(--success)' : 'var(--text-muted)'
                              }}>
                                {s.estado}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </main>

      {/* MODAL: APERTURA CAJA */}
      {isAperturaOpen && (
        <div class="modal-overlay">
          <div class="modal-card">
            <div class="modal-header">
              <h3>Aperturar Caja</h3>
              <button class="modal-close" onClick={() => setIsAperturaOpen(false)}><X size={18} /></button>
            </div>
            <div class="modal-body">
              <p style={{ color: 'var(--text-muted)', fontSize: '13px', marginBottom: '16px' }}>Ingrese la cantidad inicial de dinero en efectivo disponible para dar vueltos.</p>
              <div class="input-group">
                <label>Fondo de Caja (S/.)</label>
                <input type="number" value={cajaMontoInicial} onChange={(e) => setCajaMontoInicial(e.target.value)} />
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" onClick={() => setIsAperturaOpen(false)}>Cancelar</button>
              <button class="btn btn-success" onClick={handleExecuteApertura}>Aperturar</button>
            </div>
          </div>
        </div>
      )}

      {/* MODAL: REGISTRAR GASTO */}
      {isExpenseOpen && (
        <div class="modal-overlay">
          <div class="modal-card">
            <div class="modal-header">
              <h3>Registrar Egreso Adicional</h3>
              <button class="modal-close" onClick={() => setIsExpenseOpen(false)}><X size={18} /></button>
            </div>
            <div class="modal-body">
              <div class="input-group">
                <label>Monto a retirar (S/.)</label>
                <input type="number" value={egresoMonto} onChange={(e) => setEgresoMonto(e.target.value)} />
              </div>
              <div class="input-group">
                <label>Motivo</label>
                <textarea rows={2} value={egresoMotivo} onChange={(e) => setEgresoMotivo(e.target.value)} placeholder="Ej. Compra urgente de cilantro"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" onClick={() => setIsExpenseOpen(false)}>Cancelar</button>
              <button class="btn btn-danger" onClick={handleRegisterExpense}>Guardar Egreso</button>
            </div>
          </div>
        </div>
      )}

      {/* MODAL: REGISTRAR COMPRA / MERMA INSUMO */}
      {isInvMovOpen && (
        <div class="modal-overlay">
          <div class="modal-card">
            <div class="modal-header">
              <h3>{invMovType === 'ENTRADA' ? 'Registrar Compra / Entrada' : 'Registrar Merma / Pérdida'}</h3>
              <button class="modal-close" onClick={() => setIsInvMovOpen(false)}><X size={18} /></button>
            </div>
            <div class="modal-body">
              <div class="input-group">
                <label>Insumo</label>
                <select value={invMovInsumoId} onChange={(e) => setInvMovInsumoId(e.target.value)}>
                  {insumos.map(i => (
                    <option key={i.id} value={i.id}>{i.nombre} ({i.unidad_medida})</option>
                  ))}
                </select>
              </div>
              <div class="input-group">
                <label>Cantidad</label>
                <input type="number" value={invMovCantidad} onChange={(e) => setInvMovCantidad(e.target.value)} />
              </div>
              <div class="input-group">
                <label>Motivo</label>
                <input type="text" value={invMovMotivo} onChange={(e) => setInvMovMotivo(e.target.value)} placeholder="Ej. Lote semanal avícola / Verdura malograda" />
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" onClick={() => setIsInvMovOpen(false)}>Cancelar</button>
              <button class="btn btn-success" onClick={handleRegisterInvMovement}>Guardar Movimiento</button>
            </div>
          </div>
        </div>
      )}

      {/* MODAL: STOCK CRÍTICO */}
      {isLowStockOpen && (
        <div class="modal-overlay">
          <div class="modal-card" style={{ maxWidth: '420px' }}>
            <div class="modal-header">
              <h3><AlertTriangle style={{ color: 'var(--danger)' }} /> Alertas de Stock Crítico</h3>
              <button class="modal-close" onClick={() => setIsLowStockOpen(false)}><X size={18} /></button>
            </div>
            <div class="modal-body" style={{ maxHeight: '300px', overflowY: 'auto' }}>
              {getLowStockCount() === 0 ? (
                <div style={{ textAlign: 'center', padding: '16px', color: 'var(--success)' }}>
                  <p>Insumos estables. Ninguno por debajo del mínimo.</p>
                </div>
              ) : (
                insumos.filter(i => i.stock_actual <= i.stock_minimo).map(i => (
                  <div key={i.id} style={{ display: 'flex', justifyContent: 'space-between', backgroundColor: 'rgba(239,68,68,0.1)', padding: '12px', border: '1px solid rgba(239,68,68,0.3)', borderRadius: '6px', marginBottom: '8px' }}>
                    <div>
                      <strong style={{ fontSize: '13px' }}>{i.nombre}</strong>
                      <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Actual: {i.stock_actual.toFixed(2)} / Min: {i.stock_minimo}</div>
                    </div>
                    <span class="badge-status" style={{ backgroundColor: 'var(--danger)', color: 'white', fontSize: '10px' }}>Comprar</span>
                  </div>
                ))
              )}
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" style={{ width: '100%', justifyContent: 'center' }} onClick={() => setIsLowStockOpen(false)}>Entendido</button>
            </div>
          </div>
        </div>
      )}

      {/* MODAL: TICKET DE CIERRE */}
      {isReceiptOpen && receiptData && (
        <div class="modal-overlay">
          <div class="modal-card" style={{ maxWidth: '360px' }}>
            <div class="modal-header">
              <h3>Ticket Cierre de Caja</h3>
              <button class="modal-close" onClick={() => setIsReceiptOpen(false)}><X size={18} /></button>
            </div>
            <div class="modal-body" style={{ backgroundColor: 'var(--bg-primary)', padding: '12px' }}>
              <div class="receipt-ticket">
                <div class="receipt-header">
                  <h4>*** AQUICITO BROASTER ***</h4>
                  <p>Morales - Tarapoto</p>
                  <p>ARQUEO DIARIO DE CAJA</p>
                </div>
                <div class="receipt-row"><span>Cajero:</span><span>{receiptData.usuario}</span></div>
                <div class="receipt-row"><span>Apertura:</span><span>{receiptData.fecha_apertura}</span></div>
                <div class="receipt-row"><span>Cierre:</span><span>{receiptData.fecha_cierre}</span></div>
                <div class="receipt-divider"></div>
                <div class="receipt-row"><span>Fondo Inicial:</span><span>S/. {receiptData.monto_apertura.toFixed(2)}</span></div>
                <div class="receipt-row"><span>Ventas Turno:</span><span>S/. {receiptData.ingresos_ventas.toFixed(2)}</span></div>
                <div class="receipt-row"><span>Egresos Turno:</span><span>S/. {receiptData.egresos_adicionales.toFixed(2)}</span></div>
                <div class="receipt-divider"></div>
                <div class="receipt-row bold"><span>Efectivo Total:</span><span>S/. {receiptData.monto_cierre.toFixed(2)}</span></div>
                <div class="receipt-divider"></div>
                <p style={{ textAlign: 'center', fontSize: '10px', marginTop: '10px' }}>Comprobante Interno. ¡Buen Turno!</p>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" style={{ width: '100%', justifyContent: 'center' }} onClick={() => setIsReceiptOpen(false)}>Cerrar Reporte</button>
            </div>
          </div>
        </div>
      )}

      {/* TOASTS PORTAL */}
      <div class="toast-container">
        {toastList.map(t => (
          <div key={t.id} class={`toast toast-${t.type}`}>
            <span>{t.message}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
