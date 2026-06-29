import { getStoredAuth } from './authService.js';

const DEFAULT_API_BASE_URL = '/api';

const getApiBaseUrl = () => {
  return import.meta.env.VITE_API_URL?.replace(/\/$/, '') || DEFAULT_API_BASE_URL;
};

const buildUrl = (path) => {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  return `${getApiBaseUrl()}${normalizedPath}`;
};

const getAuthToken = () => {
  const auth = getStoredAuth();
  return auth?.token || null;
};

const requestJson = async (path, options = {}) => {
  const headers = {
    ...(options.headers || {})
  };

  const token = getAuthToken();
  if (token && !options.skipAuth) {
    headers.Authorization = `Bearer ${token}`;
  }

  if (options.body && !(options.body instanceof FormData)) {
    headers['Content-Type'] = headers['Content-Type'] || 'application/json';
  }

  const response = await fetch(buildUrl(path), {
    ...options,
    headers
  });

  const rawText = await response.text();
  let payload = null;

  if (rawText) {
    try {
      payload = JSON.parse(rawText);
    } catch {
      payload = rawText;
    }
  }

  if (!response.ok) {
    const message = payload?.message || payload?.error || 'La solicitud al backend falló';
    throw new Error(message);
  }

  if (payload && typeof payload === 'object' && 'data' in payload) {
    return payload.data;
  }

  return payload;
};

export const apiGet = (path, options = {}) => requestJson(path, { ...options, method: 'GET' });

export const apiPost = (path, body, options = {}) => requestJson(path, {
  ...options,
  method: 'POST',
  body: JSON.stringify(body)
});

export const apiPatch = (path, body, options = {}) => requestJson(path, {
  ...options,
  method: 'PATCH',
  body: JSON.stringify(body)
});

export const apiPut = (path, body, options = {}) => requestJson(path, {
  ...options,
  method: 'PUT',
  body: JSON.stringify(body)
});

export const apiDelete = (path, options = {}) => requestJson(path, {
  ...options,
  method: 'DELETE'
});

export const getProductos = () => apiGet('/productos');
export const getInsumos = () => apiGet('/inventario/insumos');
export const getMovimientos = () => apiGet('/inventario/movimientos');
export const getPedidos = (estado) => apiGet(`/pedidos${estado ? `?estado=${estado}` : ''}`);
export const getCajaEstado = (usuarioUsername) => apiGet(`/caja/estado${usuarioUsername ? `?usuarioUsername=${encodeURIComponent(usuarioUsername)}` : ''}`);
export const getResumenDiario = () => apiGet('/reportes/resumen-diario');
