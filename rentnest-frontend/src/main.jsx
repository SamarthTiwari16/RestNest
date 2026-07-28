import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.jsx';
import { AuthProvider } from './context/AuthContext.jsx';
import './styles/index.css';

window.confirm = () => true;
window.alert = () => {};

createRoot(document.getElementById('root')).render(<StrictMode><AuthProvider><App /></AuthProvider></StrictMode>);

