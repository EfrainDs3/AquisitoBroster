import React from 'react';
import { ArrowRight, Drumstick } from 'lucide-react';

export default function LoginScreen({
  loginUser,
  loginPass,
  setLoginUser,
  setLoginPass,
  onSubmit,
  onQuickLogin,
  toastList,
  previewModules,
  quickUsers,
  backendConfigured
}) {
  return (
    <div class="login-overlay">
      <div class="login-card">
        <div class="login-logo">
          <Drumstick size={32} />
        </div>
        <h2>Aquicito Broaster</h2>
        <p>Sistema Integral de Gestión de Ventas e Inventario</p>

        <div class="login-module-preview">
          {previewModules.map(module => {
            const ModuleIcon = module.icon;
            return (
              <div key={module.key} class="login-module-chip">
                <ModuleIcon size={16} />
                <div>
                  <strong>{module.title}</strong>
                  <span>Acceso por rol: {module.roles.join(' / ')}</span>
                </div>
              </div>
            );
          })}
        </div>

        <form onSubmit={onSubmit}>
          <div class="login-form">
            <div class="input-group">
              <label>Nombre de Usuario / Rol</label>
              <input 
                type="text" 
                value={loginUser} 
                onChange={(e) => setLoginUser(e.target.value)} 
                placeholder="Ej: admin, cajero1, cocinero1..." 
                required
              />
            </div>
            <div class="input-group" style={{ marginBottom: '20px' }}>
              <label>Contraseña de Acceso</label>
              <input 
                type="password" 
                value={loginPass} 
                onChange={(e) => setLoginPass(e.target.value)} 
                placeholder="••••••••••••"
              />
            </div>
            <button type="submit" class="btn" style={{ width: '100%', justifyContent: 'center' }}>
              Ingresar al Sistema <ArrowRight size={16} />
            </button>
          </div>
        </form>

        <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '20px' }}>
          <span style={{ fontSize: '11px', color: 'var(--text-muted)', fontWeight: 600, display: 'block', marginBottom: '12px' }}>
            {backendConfigured ? 'BACKEND JWT HABILITADO' : 'SELECCIÓN RÁPIDA DE ROLES PARA PRUEBAS:'}
          </span>
          <div class="quick-login-grid">
            {backendConfigured ? (
              <div style={{ color: 'var(--text-muted)', fontSize: '12px', lineHeight: '1.5', gridColumn: '1 / -1' }}>
                El login ya está preparado para JWT. Configura VITE_API_URL para apuntar al backend Spring Boot.
              </div>
            ) : (
              quickUsers.map(user => (
                <button key={user.id} class="quick-login-btn" onClick={() => onQuickLogin(user)}>
                  <span class="quick-login-title">{user.rol}</span>
                  <span class="quick-login-user">{user.nombre.split(' ')[0]}</span>
                </button>
              ))
            )}
          </div>
        </div>

        <div style={{ marginTop: '14px', color: 'var(--text-muted)', fontSize: '11px', lineHeight: '1.5' }}>
          {backendConfigured
            ? 'El sistema enviará usuario y contraseña al endpoint /api/auth/login y guardará el JWT devuelto.'
            : 'Modo demo activo: se puede navegar sin backend mientras se integra el API.'}
        </div>
      </div>

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
