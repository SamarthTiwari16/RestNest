import { useEffect, useState } from 'react';
import * as propertyApi from '../api/propertyApi.js';
import Header from '../components/Header.jsx';
import { useAuth } from '../hooks/useAuth.js';

export default function MyListings({ onCreateNew, onEdit, onGoToDashboard, onGoToListings, onGoToSearch, onGoToSaved, onGoToSentEnquiries, onGoToReceivedEnquiries, onGoToAdminReview }) {
  const { user, logout } = useAuth();
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchListings = async () => {
    try {
      setLoading(true);
      const { data } = await propertyApi.getMyProperties();
      setListings(data);
    } catch (err) {
      setError('Failed to fetch your properties. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchListings();
  }, []);

  const handleAction = async (id, actionFn) => {
    try {
      setError('');
      await actionFn(id);
      await fetchListings();
    } catch (err) {
      setError(err.response?.data?.message ?? 'Action failed. Please try again.');
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'DRAFT': return 'badge badge-draft';
      case 'PENDING_VERIFICATION': return 'badge badge-pending';
      case 'APPROVED':
      case 'ACTIVE': return 'badge badge-active';
      case 'RENTED': return 'badge badge-rented';
      case 'ARCHIVED': return 'badge badge-archived';
      default: return 'badge';
    }
  };

  return (
    <div>
      <Header
        activeTab="LISTINGS"
        onGoToDashboard={onGoToDashboard}
        onGoToListings={onGoToListings}
        onGoToReceivedEnquiries={onGoToReceivedEnquiries}
        onGoToSearch={onGoToSearch}
        onGoToSaved={onGoToSaved}
        onGoToSentEnquiries={onGoToSentEnquiries}
        onGoToAdminReview={onGoToAdminReview}
      />

      <main className="container">
        {error && <p className="form-error" style={{ marginBottom: '1.5rem' }} role="alert">{error}</p>}

        <div className="page-title-row">
          <div>
            <p className="eyebrow" style={{ margin: 0 }}>Owner Portal</p>
            <h1 style={{ marginTop: '0.2rem' }}>My Property Listings</h1>
          </div>
          <button onClick={onCreateNew}>+ Create New Listing</button>
        </div>

        {loading ? (
          <p>Loading your properties...</p>
        ) : listings.length === 0 ? (
          <div className="empty-state" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
            <svg width="120" height="120" viewBox="0 0 100 100" fill="none" stroke="var(--gold-thread)" strokeWidth="1.2" opacity="0.6" style={{ margin: '0 auto 1.5rem', display: 'block' }}>
              <rect x="10" y="10" width="80" height="80" />
              <line x1="10" y1="50" x2="60" y2="50" />
              <line x1="50" y1="10" x2="50" y2="90" />
              <line x1="50" y1="65" x2="90" y2="65" />
              <path d="M 50 35 A 15 15 0 0 1 35 50" strokeDasharray="3,3" />
              <path d="M 65 50 A 15 15 0 0 1 50 65" strokeDasharray="3,3" />
            </svg>
            <h3 style={{ fontFamily: 'Fraunces, serif', fontSize: '1.5rem', marginBottom: '0.5rem' }}>You haven't listed any properties yet.</h3>
            <p style={{ color: 'var(--fog)', marginBottom: '1.5rem' }}>Click below to list your first property and find the right tenants.</p>
            <button onClick={onCreateNew}>List Your First Property</button>
          </div>
        ) : (
          <div className="listing-grid">
            {listings.map((item) => {
              const coverImage = item.images && item.images.length > 0 ? item.images[0].imageUrl : null;
              const fullCoverUrl = coverImage ? (coverImage.startsWith('/') ? `http://localhost:8080${coverImage}` : coverImage) : null;
              return (
                <div key={item.id} className="listing-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                  <div>
                    {fullCoverUrl && (
                      <div className="listing-card-banner" style={{ width: '100%', height: '140px', overflow: 'hidden', borderRadius: '4px', marginBottom: '0.8rem' }}>
                        <img src={fullCoverUrl} alt={item.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                      </div>
                    )}
                    <div className="listing-card-header">
                      <h3 className="listing-card-title">{item.title}</h3>
                      <span className={getStatusBadgeClass(item.status)}>{item.status.replace('_', ' ')}</span>
                    </div>
                  <p className="listing-price">₹{item.rent.toLocaleString('en-IN')} / month</p>
                  <p style={{ margin: '0.5rem 0', color: 'var(--charcoal)', fontWeight: 500 }}>
                    {item.locality}, {item.city}
                  </p>
                  <div className="listing-details">
                    <span>{item.bhk} BHK</span>
                    <span>•</span>
                    <span>{item.propertyType}</span>
                    <span>•</span>
                    <span>{item.furnished ? 'Furnished' : 'Unfurnished'}</span>
                    <span>•</span>
                    <span>{item.parking ? 'Parking' : 'No Parking'}</span>
                    <span>•</span>
                    <span>{item.petFriendly ? 'Pet Friendly' : 'No Pets'}</span>
                  </div>
                  {item.status === 'DRAFT' && item.rejectionReason && (
                    <div style={{ marginTop: '0.75rem', padding: '0.65rem 0.85rem', background: '#FFF0EC', border: '1px solid #FFDCD4', borderRadius: '6px', fontSize: '0.825rem', color: 'var(--clay)', lineHeight: 1.4 }}>
                      <strong>Feedback:</strong> {item.rejectionReason}
                    </div>
                  )}
                </div>

                <div className="listing-actions" style={{ marginTop: '1.5rem' }}>
                  {item.status === 'DRAFT' && (
                    <>
                      <button 
                        className="btn-secondary" 
                        onClick={() => onEdit(item)}
                      >
                        Edit
                      </button>
                      <button 
                        onClick={() => handleAction(item.id, propertyApi.submitForVerification)}
                      >
                        Submit Verification
                      </button>
                    </>
                  )}
                  {/* For testing, allow developer to mark as active directly if needed, 
                      or show mark as rented / withdraw if active */}
                  {(item.status === 'ACTIVE' || item.status === 'APPROVED') && (
                    <>
                      <button 
                        onClick={() => handleAction(item.id, propertyApi.markAsRented)}
                      >
                        Mark Rented
                      </button>
                      <button 
                        className="btn-secondary btn-danger" 
                        onClick={() => handleAction(item.id, propertyApi.withdrawProperty)}
                      >
                        Withdraw
                      </button>
                    </>
                  )}
                  {item.status === 'PENDING_VERIFICATION' && (
                    <p style={{ fontSize: '0.8rem', color: 'var(--fog)', margin: 0, fontStyle: 'italic' }}>
                      Awaiting admin approval...
                    </p>
                  )}
                </div>
              </div>
            ); })}
          </div>
        )}
      </main>
    </div>
  );
}
