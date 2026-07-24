import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import * as authApi from '../api/authApi.js';
export const AuthContext = createContext(null);
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const clearSession = useCallback(() => { sessionStorage.removeItem('rentnest_access_token'); setUser(null); }, []);
  useEffect(() => { const restore = async () => { if (!sessionStorage.getItem('rentnest_access_token')) { setIsLoading(false); return; } try { const { data } = await authApi.getCurrentUser(); setUser(data); } catch { clearSession(); } finally { setIsLoading(false); } }; restore(); }, [clearSession]);
  const authenticate = useCallback((response) => { sessionStorage.setItem('rentnest_access_token', response.accessToken); setUser(response.user); }, []);
  const value = useMemo(() => ({ user, isLoading, authenticate, logout: clearSession }), [user, isLoading, authenticate, clearSession]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
