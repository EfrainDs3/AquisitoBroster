import React from 'react';
import { Check, Plus } from 'lucide-react';

export default function PosView({
  productos,
  cart,
  cartCustomerName,
  posCategoryFilter,
  setPosCategoryFilter,
  setCartCustomerName,
  addToCart,
  updateCartQty,
  clearCart,
  getCartTotal,
  handleRegisterOrder
}) {
  return (
    <div class="pos-grid">
      <div class="pos-main">
        <div class="category-filter">
          <button class={`category-btn ${posCategoryFilter === 'all' ? 'active' : ''}`} onClick={() => setPosCategoryFilter('all')}>Todos</button>
          <button class={`category-btn ${posCategoryFilter === 'combos' ? 'active' : ''}`} onClick={() => setPosCategoryFilter('combos')}>Combos</button>
          <button class={`category-btn ${posCategoryFilter === 'individuales' ? 'active' : ''}`} onClick={() => setPosCategoryFilter('individuales')}>Individuales</button>
        </div>

        <div class="products-grid">
          {productos
            .filter(p => posCategoryFilter === 'all' || p.categoria === posCategoryFilter)
            .map(p => (
              <div key={p.id} class="product-card" onClick={() => addToCart(p.id)}>
                <span class="product-tag">Disponible</span>
                <div class="product-name">{p.nombre}</div>
                <div class="product-desc">{p.desc}</div>
                <div class="product-price">S/. {p.precio.toFixed(2)}</div>
              </div>
            ))}
        </div>
      </div>

      <div class="pos-sidebar">
        <div class="cart-header">
          <h3>Pedido Activo</h3>
          <button class="btn btn-secondary" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={clearCart}>Vaciar</button>
        </div>

        <div class="cart-items">
          {cart.length === 0 ? (
            <div class="cart-empty">Carrito vacío</div>
          ) : (
            cart.map(item => (
              <div key={item.id} class="cart-item">
                <div class="cart-item-info">
                  <div class="cart-item-name">{item.nombre}</div>
                  <div class="cart-item-price">S/. {item.precio.toFixed(2)}</div>
                </div>
                <div class="cart-item-qty">
                  <button class="qty-btn" onClick={() => updateCartQty(item.id, -1)}>-</button>
                  <span>{item.qty}</span>
                  <button class="qty-btn" onClick={() => updateCartQty(item.id, 1)}>+</button>
                </div>
                <div class="cart-item-subtotal">S/. {(item.precio * item.qty).toFixed(2)}</div>
              </div>
            ))
          )}
        </div>

        <div>
          <div class="cart-customer-section">
            <div class="input-group">
              <label>Cliente / Mesa</label>
              <input type="text" value={cartCustomerName} onChange={(e) => setCartCustomerName(e.target.value)} />
            </div>
          </div>

          <div class="cart-totals">
            <div class="cart-total-row">
              <span>Subtotal</span>
              <span>S/. {getCartTotal().toFixed(2)}</span>
            </div>
            <div class="cart-total-row grand-total">
              <span>Total a pagar</span>
              <span>S/. {getCartTotal().toFixed(2)}</span>
            </div>
          </div>

          <button class="btn btn-success" style={{ width: '100%', justifyContent: 'center' }} onClick={handleRegisterOrder}>
            <Check size={16} /> Registrar Pedido
          </button>
        </div>
      </div>
    </div>
  );
}
