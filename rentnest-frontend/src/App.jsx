import { useState } from 'react';
import Login from './pages/Auth/Login.jsx';
import Register from './pages/Auth/Register.jsx';
import Dashboard from './pages/Dashboard.jsx';
import MyListings from './pages/MyListings.jsx';
import CreateListing from './pages/CreateListing.jsx';
import Search from './pages/Search.jsx';
import SavedListings from './pages/SavedListings.jsx';
import SentEnquiries from './pages/SentEnquiries.jsx';
import ReceivedEnquiries from './pages/ReceivedEnquiries.jsx';
import { useAuth } from './hooks/useAuth.js';

export default function App() {
  const { user, isLoading } = useAuth();
  const [isRegistering, setIsRegistering] = useState(false);
  const [view, setView] = useState('DASHBOARD');
  const [editingProperty, setEditingProperty] = useState(null);

  if (isLoading) return <main className="auth-shell"><p className="loading">Loading your session…</p></main>;

  if (user) {
    const navProps = {
      onGoToDashboard: () => setView('DASHBOARD'),
      onGoToListings: () => setView('LISTINGS'),
      onGoToSearch: () => setView('SEARCH'),
      onGoToSaved: () => setView('SAVED'),
      onGoToSentEnquiries: () => setView('MY_ENQUIRIES'),
      onGoToReceivedEnquiries: () => setView('RECEIVED_ENQUIRIES'),
    };

    if (view === 'DASHBOARD') return <Dashboard {...navProps} />;
    if (view === 'CREATE') {
      return (
        <CreateListing 
          propertyToEdit={editingProperty}
          onCancel={() => { setView('LISTINGS'); setEditingProperty(null); }}
          onSuccess={() => { setView('LISTINGS'); setEditingProperty(null); }}
        />
      );
    }
    if (view === 'SEARCH') return <Search {...navProps} />;
    if (view === 'SAVED') return <SavedListings {...navProps} />;
    if (view === 'MY_ENQUIRIES') return <SentEnquiries {...navProps} />;
    if (view === 'RECEIVED_ENQUIRIES') return <ReceivedEnquiries {...navProps} />;

    // Default: LISTINGS
    return (
      <MyListings
        {...navProps}
        onCreateNew={() => { setEditingProperty(null); setView('CREATE'); }}
        onEdit={(property) => { setEditingProperty(property); setView('CREATE'); }}
      />
    );
  }

  return isRegistering ? <Register onSwitch={() => setIsRegistering(false)} /> : <Login onSwitch={() => setIsRegistering(true)} />;
}

