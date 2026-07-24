import { useState } from 'react';
import * as authApi from '../../api/authApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { AuthCard } from './Login.jsx';

export default function Register({ onSwitch }) {
  const { authenticate } = useAuth();
  const [form, setForm] = useState({ name: '', email: '', phone: '', password: '' });
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  async function submit(event) {
    event.preventDefault(); setError(''); setIsSubmitting(true);
    try { const { data } = await authApi.register(form); authenticate(data); }
    catch (requestError) { setError(requestError.response?.data?.message ?? 'Unable to create your account. Please try again.'); }
    finally { setIsSubmitting(false); }
  }
  return <AuthCard title="Begin a new chapter" subtitle="One account for finding and listing a home.">
    <form onSubmit={submit} className="auth-form">
      <label>Full name<input required maxLength="100" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></label>
      <label>Email<input type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
      <label>Indian mobile number<input type="tel" required placeholder="9876543210" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} /></label>
      <label>Password<input type="password" required minLength="8" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /><span>8+ characters with upper/lowercase, number and symbol.</span></label>
      {error && <p className="form-error" role="alert">{error}</p>}
      <button type="submit" disabled={isSubmitting}>{isSubmitting ? 'Creating account…' : 'Create account'}</button>
    </form>
    <p className="auth-switch">Already have an account? <button type="button" className="text-button" onClick={onSwitch}>Sign in</button></p>
  </AuthCard>;
}
