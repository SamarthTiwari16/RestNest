import { useAuth } from '../hooks/useAuth.js';

export default function Header({ activeTab, onGoToDashboard, onGoToListings, onGoToReceivedEnquiries, onGoToSearch, onGoToSaved, onGoToSentEnquiries, onGoToAdminReview }) {
  const { user, logout } = useAuth();

  return (
    <header className="navbar" style={{ padding: '1rem 2rem' }}>
      <h1 style={{ fontFamily: 'Fraunces, serif', fontSize: '1.5rem', color: 'var(--parchment)', margin: 0 }}>RentNest</h1>
      <div className="nav-links" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
        <button className={`nav-link-btn ${activeTab === 'DASHBOARD' ? 'active' : ''}`} onClick={onGoToDashboard}>
          Dashboard
        </button>
        <button className={`nav-link-btn ${activeTab === 'LISTINGS' ? 'active' : ''}`} onClick={onGoToListings}>
          My Listings
        </button>
        <button className={`nav-link-btn ${activeTab === 'RECEIVED_ENQUIRIES' ? 'active' : ''}`} onClick={onGoToReceivedEnquiries}>
          Received Enquiries
        </button>
        <button className={`nav-link-btn ${activeTab === 'SEARCH' ? 'active' : ''}`} onClick={onGoToSearch}>
          Search Properties
        </button>
        <button className={`nav-link-btn ${activeTab === 'SAVED' ? 'active' : ''}`} onClick={onGoToSaved}>
          Saved Properties
        </button>
        <button className={`nav-link-btn ${activeTab === 'MY_ENQUIRIES' ? 'active' : ''}`} onClick={onGoToSentEnquiries}>
          My Enquiries
        </button>
        {user?.role === 'ROLE_ADMIN' && (
          <button className={`nav-link-btn ${activeTab === 'ADMIN_REVIEW' ? 'active' : ''}`} onClick={onGoToAdminReview}>
            Admin Review
          </button>
        )}
        <span style={{ marginLeft: '1rem', color: 'var(--parchment)', fontSize: '0.9rem', opacity: 0.8 }}>
          Hello, {user?.name}
        </span>
        <button 
          className="btn-secondary" 
          style={{ color: 'var(--parchment)', borderColor: 'var(--parchment)', padding: '0.4rem 0.8rem', borderRadius: '6px' }} 
          onClick={logout}
        >
          Sign out
        </button>
      </div>
    </header>
  );
}
