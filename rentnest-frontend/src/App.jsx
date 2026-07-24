import { useState } from 'react';
import Login from './pages/Auth/Login.jsx';
import Register from './pages/Auth/Register.jsx';
import { useAuth } from './hooks/useAuth.js';

function AccountHome() {
  const { user, logout } = useAuth();
  return <main className="account-shell"><section className="account-card"><p className="eyebrow">RentNest</p><h1>Hello, {user.name}</h1><p>You are securely signed in. Your account is ready for Phase 2 listings.</p><dl><div><dt>Email</dt><dd>{user.email}</dd></div><div><dt>Phone</dt><dd>{user.phone}</dd></div></dl><button onClick={logout}>Sign out</button></section></main>;
}

export default function App() {
  const { user, isLoading } = useAuth();
  const [isRegistering, setIsRegistering] = useState(false);
  if (isLoading) return <main className="auth-shell"><p className="loading">Loading your session…</p></main>;
  if (user) return <AccountHome />;
  return isRegistering ? <Register onSwitch={() => setIsRegistering(false)} /> : <Login onSwitch={() => setIsRegistering(true)} />;
}
