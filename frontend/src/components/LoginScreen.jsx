import React from 'react';
import { ArrowRight, Drumstick } from 'lucide-react';

export default function LoginScreen({
  loginUser,
  loginPass,
  setLoginUser,
  setLoginPass,
  onSubmit,
  toastList
}) {
  return (
    <div class="login-overlay">
      <div class="login-card">
        <div class="login-logo">
          <Drumstick size={32} />
        </div>
        <h2>Aquicito Broaster</h2>
        <p>Sistema Integral de Gestión de Ventas e Inventario</p>

        <form onSubmit={onSubmit}>
          <div class="login-form">
            <div class="input-group">
              <label>Nombre de Usuario</label>
              <input
                type="text"
                value={loginUser}
                onChange={(e) => setLoginUser(e.target.value)}
                placeholder="Ej: admin, cajero1, cocinero1..."
                required
              />
            </div>
            <div class="input-group" style={{ marginBottom: '20px' }}>
              <label>Contraseña</label>
              <input
                type="password"
                value={loginPass}
                onChange={(e) => setLoginPass(e.target.value)}
                placeholder="••••••••"
              />
            </div>
            <button type="submit" class="btn" style={{ width: '100%', justifyContent: 'center' }}>
              Ingresar al Sistema <ArrowRight size={16} />
            </button>
          </div>
        </form>
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
