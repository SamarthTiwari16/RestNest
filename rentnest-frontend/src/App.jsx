import { useState } from 'react';
import Login from './pages/Auth/Login.jsx';
import Register from './pages/Auth/Register.jsx';
import MyListings from './pages/MyListings.jsx';
import CreateListing from './pages/CreateListing.jsx';
import Search from './pages/Search.jsx';
import { useAuth } from './hooks/useAuth.js';

export default function App() {
  const { user, isLoading } = useAuth();
  const [isRegistering, setIsRegistering] = useState(false);
  const [view, setView] = useState('LISTINGS');
  const [editingProperty, setEditingProperty] = useState(null);

  if (isLoading) return <main className="auth-shell"><p className="loading">Loading your session…</p></main>;

  if (user) {
    if (view === 'CREATE') {
      return (
        <CreateListing 
          propertyToEdit={editingProperty}
          onCancel={() => {
            setView('LISTINGS');
            setEditingProperty(null);
          }}
          onSuccess={() => {
            setView('LISTINGS');
            setEditingProperty(null);
          }}
        />
      );
    }
    if (view === 'SEARCH') {
      return (
        <Search 
          onGoToListings={() => setView('LISTINGS')}
          onGoToSearch={() => setView('SEARCH')}
        />
      );
    }
    return (
      <MyListings 
        onGoToListings={() => setView('LISTINGS')}
        onGoToSearch={() => setView('SEARCH')}
        onCreateNew={() => {
          setEditingProperty(null);
          setView('CREATE');
        }}
        onEdit={(property) => {
          setEditingProperty(property);
          setView('CREATE');
        }}
      />
    );
  }

  return isRegistering ? <Register onSwitch={() => setIsRegistering(false)} /> : <Login onSwitch={() => setIsRegistering(true)} />;
}
