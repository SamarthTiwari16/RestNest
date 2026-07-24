import { useState } from 'react';
import * as authApi from '../../api/authApi.js';
import { useAuth } from '../../hooks/useAuth.js';

export function AuthCard({ title, subtitle, children }) {
  return <main className="auth-shell"><section className="auth-card"><p className="eyebrow">RentNest</p><h1>{title}</h1><p className="auth-subtitle">{subtitle}</p>{children}</section></main>;
}

export default function Login({ onSwitch }) {
  const { authenticate } = useAuth();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  async function submit(event) {
    event.preventDefault(); setError(''); setIsSubmitting(true);
    try { const { data } = await authApi.login(form); authenticate(data); }
    catch (requestError) { setError(requestError.response?.data?.message ?? 'Unable to sign in. Please try again.'); }
    finally { setIsSubmitting(false); }
  }
  return <AuthCard title="Welcome back" subtitle="Continue your next chapter with RentNest.">
    <form onSubmit={submit} className="auth-form">
      <label>Email<input type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
      <label>Password<input type="password" required value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></label>
      {error && <p className="form-error" role="alert">{error}</p>}
      <button type="submit" disabled={isSubmitting}>{isSubmitting ? 'Signing in…' : 'Sign in'}</button>
    </form>
    <p className="auth-switch">New here? <button type="button" className="text-button" onClick={onSwitch}>Create an account</button></p>
  </AuthCard>;
}
