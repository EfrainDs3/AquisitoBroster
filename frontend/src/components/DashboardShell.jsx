import React from 'react';
import { BarChart3, Bell, FolderOpen, Landmark, LogOut, RotateCcw, ShieldAlert, Flame, UtensilsCrossed, Drumstick } from 'lucide-react';

export default function DashboardShell({
  activeView,
  currentUser,
  onSetActiveView,
  onResetPrototype,
  onLogout,
  onRoleChange,
  lowStockCount,
  onOpenLowStock,
  children,
  moduleHub
}) {
  const titleMap = {
    reports: 'Dashboard',
    pos: 'Punto de Venta (POS)',
    kitchen: 'Monitoreo de Cocina',
    inventory: 'Gestión de Inventario y Recetas',
    cash: 'Arqueo y Control de Caja'
  };

  return (
    <div class="app-container">
      <aside>
        <div>
          <div class="sidebar-brand">
            <Drumstick size={28} />
            <h1>Aquicito Broaster</h1>
          </div>
          <ul class="sidebar-menu">
            <li class={activeView === 'reports' ? 'active' : ''}>
              <button onClick={() => onSetActiveView('reports')}><BarChart3 size={16} /> Dashboard</button>
            </li>
            <li class={activeView === 'pos' ? 'active' : ''}>
              <button onClick={() => onSetActiveView('pos')}><UtensilsCrossed size={16} /> POS Ventas</button>
            </li>
            <li class={activeView === 'kitchen' ? 'active' : ''}>
              <button onClick={() => onSetActiveView('kitchen')}><Flame size={16} /> Vista Cocina</button>
            </li>
            <li class={activeView === 'inventory' ? 'active' : ''}>
              <button onClick={() => onSetActiveView('inventory')}><FolderOpen size={16} /> Inventario</button>
            </li>
            <li class={activeView === 'cash' ? 'active' : ''}>
              <button onClick={() => onSetActiveView('cash')}><Landmark size={16} /> Control Caja</button>
            </li>
          </ul>
        </div>
        <div>
          <ul class="sidebar-menu" style={{ paddingTop: 0 }}>
            <li>
              <button onClick={onResetPrototype} style={{ color: 'var(--text-muted)' }}><RotateCcw size={16} /> Restablecer</button>
            </li>
            <li>
              <button onClick={onLogout} style={{ color: 'var(--danger)' }}><LogOut size={16} /> Cerrar Sesión</button>
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

      <main>
        <header>
          <div class="header-title-section">
            <h2>{titleMap[activeView]}</h2>
          </div>

          <div class="header-controls">
            <div class="role-switcher-container">
              <label>Ver como:</label>
              <select value={currentUser.rol} onChange={(e) => onRoleChange(e.target.value)}>
                <option value="ADMIN">Administrador</option>
                <option value="CAJERO">Cajero</option>
                <option value="COCINA">Cocinero</option>
                <option value="ALMACEN">Almacenero</option>
              </select>
            </div>

            <div class="alert-bell" onClick={onOpenLowStock}>
              <Bell size={20} />
              {lowStockCount > 0 && <span class="alert-badge">{lowStockCount}</span>}
            </div>
          </div>
        </header>

        <div class="content-area">
          {moduleHub}
          {children}
        </div>
      </main>
    </div>
  );
}
