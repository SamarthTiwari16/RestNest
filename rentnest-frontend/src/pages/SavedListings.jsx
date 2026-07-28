import { useState, useEffect } from 'react';
import * as favouritesApi from '../api/favouritesApi.js';
import PropertyDetailsModal from '../components/property/PropertyDetailsModal.jsx';
import { useAuth } from '../hooks/useAuth.js';

export default function SavedListings({ onGoToDashboard, onGoToListings, onGoToSearch, onGoToSaved, onGoToSentEnquiries, onGoToReceivedEnquiries, onGoToAdminReview }) {
  const { user, logout } = useAuth();
  const [savedListings, setSavedListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedProperty, setSelectedProperty] = useState(null);

  const fetchSavedListings = async () => {
    try {
      setLoading(true);
      setError('');
      const { data } = await favouritesApi.getMyFavourites();
      setSavedListings(data);
    } catch (err) {
      setError('Failed to fetch saved properties. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSavedListings();
  }, []);

  const handleUnsave = async (propertyId) => {
    // Optimistic state update
    setSavedListings(prev => prev.filter(item => item.id !== propertyId));
    
    try {
      await favouritesApi.removeFavourite(propertyId);
    } catch (err) {
      // Revert if API fails
      fetchSavedListings();
    }
  };

  return (
    <div>
      <header className="navbar">
        <h1>RentNest</h1>
        <div className="nav-links" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <button 
            onClick={onGoToDashboard}
            style={{ background: 'transparent', border: 'none', color: 'var(--parchment)', cursor: 'pointer', fontSize: '0.9rem' }}
          >
            Dashboard
          </button>
          <button 
            onClick={onGoToListings}
            style={{ background: 'transparent', border: 'none', color: 'var(--parchment)', cursor: 'pointer', fontSize: '0.9rem' }}
          >
            My Listings
          </button>
          <button 
            onClick={onGoToReceivedEnquiries}
            style={{ background: 'transparent', border: 'none', color: 'var(--parchment)', cursor: 'pointer', fontSize: '0.9rem' }}
          >
            Received Enquiries
          </button>
          <button 
            onClick={onGoToSearch}
            style={{ background: 'transparent', border: 'none', color: 'var(--parchment)', cursor: 'pointer', fontSize: '0.9rem' }}
          >
            Search Properties
          </button>
          <button 
            onClick={onGoToSaved}
            style={{ background: 'transparent', border: 'none', color: 'var(--parchment)', cursor: 'pointer', fontSize: '0.9rem', borderBottom: '2px solid var(--parchment)', fontWeight: 'bold' }}
          >
            Saved Properties
          </button>
          <button 
            onClick={onGoToSentEnquiries}
            style={{ background: 'transparent', border: 'none', color: 'var(--parchment)', cursor: 'pointer', fontSize: '0.9rem' }}
          >
            My Enquiries
          </button>
          {user.role === 'ROLE_ADMIN' && (
            <button 
              onClick={onGoToAdminReview}
              style={{ background: 'transparent', border: 'none', color: 'var(--parchment)', cursor: 'pointer', fontSize: '0.9rem' }}
            >
              Admin Review
            </button>
          )}
          <span className="nav-link" style={{ marginLeft: '1rem' }}>Hello, {user.name}</span>
          <button className="btn-secondary" style={{ color: 'var(--parchment)', borderColor: 'var(--parchment)' }} onClick={logout}>Sign out</button>
        </div>
      </header>

      <main className="container" style={{ maxWidth: '1200px' }}>
        <div className="page-title-row" style={{ marginBottom: '2rem' }}>
          <div>
            <p className="eyebrow" style={{ margin: 0 }}>Tenant Portal</p>
            <h1 style={{ marginTop: '0.2rem' }}>Saved Properties</h1>
          </div>
        </div>

        {error && <p className="form-error" role="alert" style={{ marginBottom: '1.5rem' }}>{error}</p>}

        {loading ? (
          <p style={{ fontSize: '1.2rem', fontStyle: 'italic', color: 'var(--fog)' }}>Loading your saved homes...</p>
        ) : savedListings.length === 0 ? (
          <div className="empty-state" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
            <h3 style={{ fontFamily: 'Fraunces, serif', fontSize: '1.6rem', marginBottom: '0.5rem' }}>Your saved list is empty.</h3>
            <p style={{ color: 'var(--fog)' }}>Browse homes in the explorer tab and click the heart icon to save them here.</p>
            <button style={{ marginTop: '1.5rem' }} onClick={onGoToSearch}>Search Homes</button>
          </div>
        ) : (
          <div>
            <p style={{ margin: '0 0 1.5rem 0', fontWeight: '500', color: 'var(--fog)', fontSize: '0.9rem' }}>
              You have {savedListings.length} saved properties
            </p>

            <div className="listing-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))' }}>
              {savedListings.map((item) => {
                const coverImage = item.images && item.images.length > 0 ? item.images[0].imageUrl : null;
                const fullCoverUrl = coverImage ? (coverImage.startsWith('/') ? `http://localhost:8080${coverImage}` : coverImage) : null;
                return (
                  <div key={item.id} className="listing-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between', position: 'relative' }}>
                    
                    {/* Unsave Heart Overlay Button */}
                    <button
                      onClick={() => handleUnsave(item.id)}
                      aria-label="Remove from saved properties"
                      style={{
                        position: 'absolute',
                        top: '1rem',
                        right: '1rem',
                        background: 'rgba(251, 248, 241, 0.95)',
                        border: 'none',
                        borderRadius: '50%',
                        width: '2.5rem',
                        height: '2.5rem',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        cursor: 'pointer',
                        padding: 0,
                        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                        zIndex: 10
                      }}
                    >
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="var(--clay)">
                        <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                      </svg>
                    </button>

                    <div>
                      {fullCoverUrl ? (
                        <div style={{ width: '100%', height: '160px', overflow: 'hidden', borderRadius: '4px', marginBottom: '0.8rem' }}>
                          <img src={fullCoverUrl} alt={item.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                        </div>
                      ) : (
                        <div style={{ width: '100%', height: '160px', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: 'var(--clay)', borderRadius: '4px', marginBottom: '0.8rem', color: 'var(--fog)', fontStyle: 'italic', fontSize: '0.8rem' }}>
                          No photos available
                        </div>
                      )}
                      <div className="listing-card-header">
                        <h3 className="listing-card-title">{item.title}</h3>
                      </div>
                      <p className="listing-price">₹{item.rent.toLocaleString('en-IN')} / month</p>
                      <p style={{ margin: '0.5rem 0', color: 'var(--charcoal)', fontWeight: 500 }}>
                        {item.locality}, {item.city}
                      </p>
                      <div className="listing-details" style={{ fontSize: '0.75rem' }}>
                        <span>{item.bhk} BHK</span>
                        <span>•</span>
                        <span>{item.propertyType.replace('_', ' ')}</span>
                        <span>•</span>
                        <span>{item.furnished ? 'Furnished' : 'Unfurnished'}</span>
                        {item.parking && <span>• Parking</span>}
                        {item.petFriendly && <span>• Pet Friendly</span>}
                      </div>
                    </div>

                    <div className="listing-actions" style={{ marginTop: '1.2rem' }}>
                      <button style={{ width: '100%' }} onClick={() => setSelectedProperty(item)}>View Details</button>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </main>

      {selectedProperty && (
        <PropertyDetailsModal 
          property={selectedProperty} 
          onClose={() => setSelectedProperty(null)}
          onEnquirySuccess={() => fetchSavedListings()}
        />
      )}
    </div>
  );
}
