const DEFAULT_API_BASE_URL = 'http://localhost:8080/api';

const getApiBaseUrl = () => {
  return import.meta.env.VITE_API_URL?.replace(/\/$/, '') || DEFAULT_API_BASE_URL;
};

const isBackendConfigured = () => Boolean(import.meta.env.VITE_API_URL);

const buildLoginUrl = () => `${getApiBaseUrl()}/auth/login`;

export async function loginWithJwt(username, password) {
  if (!isBackendConfigured()) {
    throw new Error('Backend no configurado. Define VITE_API_URL para activar JWT.');
  }

  const response = await fetch(buildLoginUrl(), {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, password })
  });

  const payload = await response.json().catch(() => null);

  if (!response.ok) {
    throw new Error(payload?.message || 'No se pudo iniciar sesión');
  }

  return payload;
}

export function getStoredAuth() {
  const raw = localStorage.getItem('aquicito_broaster_auth');
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function saveStoredAuth(authData) {
  localStorage.setItem('aquicito_broaster_auth', JSON.stringify(authData));
}

export function clearStoredAuth() {
  localStorage.removeItem('aquicito_broaster_auth');
}

export function hasBackendConfigured() {
  return isBackendConfigured();
}
