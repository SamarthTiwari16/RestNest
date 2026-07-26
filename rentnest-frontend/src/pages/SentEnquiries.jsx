import { useState, useEffect } from 'react';
import * as enquiriesApi from '../api/enquiriesApi.js';
import { useAuth } from '../hooks/useAuth.js';

export default function SentEnquiries({ onGoToDashboard, onGoToListings, onGoToSearch, onGoToSaved, onGoToSentEnquiries, onGoToReceivedEnquiries }) {
  const { user, logout } = useAuth();
  const [enquiries, setEnquiries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchEnquiries = async () => {
    try {
      setLoading(true);
      setError('');
      const { data } = await enquiriesApi.getSentEnquiries();
      setEnquiries(data);
    } catch (err) {
      setError('Failed to fetch your submitted enquiries.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEnquiries();
  }, []);

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
            style={{ background: 'transparent', border: 'none', color: 'var(--parchment)', cursor: 'pointer', fontSize: '0.9rem' }}
          >
            Saved Properties
          </button>
          <button 
            onClick={onGoToSentEnquiries}
            style={{ background: 'transparent', border: 'none', color: 'var(--parchment)', cursor: 'pointer', fontSize: '0.9rem', borderBottom: '2px solid var(--parchment)', fontWeight: 'bold' }}
          >
            My Enquiries
          </button>
          <span className="nav-link" style={{ marginLeft: '1rem' }}>Hello, {user.name}</span>
          <button className="btn-secondary" style={{ color: 'var(--parchment)', borderColor: 'var(--parchment)' }} onClick={logout}>Sign out</button>
        </div>
      </header>

      <main className="container" style={{ maxWidth: '1000px' }}>
        <div className="page-title-row" style={{ marginBottom: '2rem' }}>
          <div>
            <p className="eyebrow" style={{ margin: 0 }}>Tenant Portal</p>
            <h1 style={{ marginTop: '0.2rem' }}>My Enquiries</h1>
          </div>
        </div>

        {error && <p className="form-error" role="alert" style={{ marginBottom: '1.5rem' }}>{error}</p>}

        {loading ? (
          <p style={{ fontSize: '1.2rem', fontStyle: 'italic', color: 'var(--fog)' }}>Loading your enquiries...</p>
        ) : enquiries.length === 0 ? (
          <div className="empty-state" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
            <h3 style={{ fontFamily: 'Fraunces, serif', fontSize: '1.6rem', marginBottom: '0.5rem' }}>No enquiries submitted.</h3>
            <p style={{ color: 'var(--fog)' }}>When you enquire on listing cards, their details and status updates will show up here.</p>
            <button style={{ marginTop: '1.5rem' }} onClick={onGoToSearch}>Browse Listings</button>
          </div>
        ) : (
          <div style={{ display: 'grid', gap: '1.5rem' }}>
            {enquiries.map((enq) => {
              const coverImage = enq.property.images && enq.property.images.length > 0 ? enq.property.images[0].imageUrl : null;
              const fullCoverUrl = coverImage ? (coverImage.startsWith('/') ? `http://localhost:8080${coverImage}` : coverImage) : null;
              return (
                <div key={enq.id} className="listing-card" style={{ display: 'grid', gridTemplateColumns: '180px 1fr', gap: '1.5rem', margin: 0, padding: '1.5rem' }}>
                  
                  {/* Left Side: Thumbnail */}
                  {fullCoverUrl ? (
                    <div style={{ width: '100%', height: '130px', overflow: 'hidden', borderRadius: '4px' }}>
                      <img src={fullCoverUrl} alt={enq.property.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                    </div>
                  ) : (
                    <div style={{ width: '100%', height: '130px', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: 'var(--clay)', borderRadius: '4px', color: 'var(--fog)', fontStyle: 'italic', fontSize: '0.8rem' }}>
                      No photos
                    </div>
                  )}

                  {/* Right Side: Details & Actions */}
                  <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                    <div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <h3 style={{ fontFamily: 'Fraunces, serif', fontSize: '1.25rem', margin: 0, color: 'var(--ink)' }}>{enq.property.title}</h3>
                        <span className={`badge ${
                          enq.status === 'ACCEPTED' ? 'badge-active' : enq.status === 'DECLINED' ? 'badge-rented' : 'badge-pending'
                        }`}>
                          {enq.status}
                        </span>
                      </div>
                      <p style={{ margin: '0.2rem 0', fontWeight: '600', color: 'var(--clay)' }}>₹{enq.property.rent.toLocaleString('en-IN')} / month</p>
                      <p style={{ margin: '0.4rem 0 0.8rem 0', color: 'var(--charcoal)', fontSize: '0.88rem' }}>
                        {enq.property.locality}, {enq.property.city} • {enq.property.bhk} BHK • {enq.property.propertyType.replace('_', ' ')}
                      </p>

                      <div style={{ background: 'var(--parchment)', padding: '0.8rem', borderRadius: '6px', fontSize: '0.85rem', marginBottom: '0.8rem' }}>
                        <div style={{ margin: '0.2rem 0' }}><strong>Move-in Date:</strong> {new Date(enq.moveInDate).toLocaleDateString('en-IN', { year: 'numeric', month: 'long', day: 'numeric' })}</div>
                        <div style={{ margin: '0.2rem 0' }}><strong>Occupants:</strong> {enq.occupants}</div>
                        {enq.message && <div style={{ margin: '0.4rem 0 0 0', fontStyle: 'italic', color: 'var(--charcoal)' }}>"{enq.message}"</div>}
                      </div>
                    </div>

                    {/* Reveal Contact Details block */}
                    {enq.status === 'ACCEPTED' ? (
                      <div style={{ borderTop: '1px solid rgb(138 130 114 / 25%)', paddingTop: '0.8rem', display: 'flex', flexWrap: 'wrap', gap: '1.5rem', fontSize: '0.85rem' }}>
                        <div><strong>Owner Contact Details Released:</strong></div>
                        <div><strong>Name:</strong> {enq.owner.name}</div>
                        <div><strong>Email:</strong> <a href={`mailto:${enq.owner.email}`}>{enq.owner.email}</a></div>
                        <div><strong>Phone:</strong> <a href={`tel:${enq.owner.phone}`}>{enq.owner.phone}</a></div>
                      </div>
                    ) : enq.status === 'DECLINED' ? (
                      <div style={{ fontSize: '0.82rem', color: 'var(--clay)', fontStyle: 'italic' }}>
                        The owner has declined this enquiry. Contact details are not available.
                      </div>
                    ) : (
                      <div style={{ fontSize: '0.82rem', color: 'var(--fog)', fontStyle: 'italic' }}>
                        Your enquiry is pending owner response. Contact details will be unlocked once accepted.
                      </div>
                    )}

                  </div>

                </div>
              );
            })}
          </div>
        )}
      </main>
    </div>
  );
}
