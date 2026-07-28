import { useState, useEffect } from 'react';
import * as enquiriesApi from '../api/enquiriesApi.js';
import { useAuth } from '../hooks/useAuth.js';

export default function ReceivedEnquiries({ onGoToDashboard, onGoToListings, onGoToSearch, onGoToSaved, onGoToSentEnquiries, onGoToReceivedEnquiries, onGoToAdminReview }) {
  const { user, logout } = useAuth();
  const [enquiries, setEnquiries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionLoading, setActionLoading] = useState(null); // holds enquiryId currently updating

  const fetchEnquiries = async () => {
    try {
      setLoading(true);
      setError('');
      const { data } = await enquiriesApi.getReceivedEnquiries();
      setEnquiries(data);
    } catch (err) {
      setError('Failed to fetch received enquiries.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEnquiries();
  }, []);

  const handleAccept = async (enquiryId) => {
    try {
      setActionLoading(enquiryId);
      setError('');
      await enquiriesApi.acceptEnquiry(enquiryId);
      await fetchEnquiries();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to accept enquiry.');
    } finally {
      setActionLoading(null);
    }
  };

  const handleDecline = async (enquiryId) => {
    try {
      setActionLoading(enquiryId);
      setError('');
      await enquiriesApi.declineEnquiry(enquiryId);
      await fetchEnquiries();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to decline enquiry.');
    } finally {
      setActionLoading(null);
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
            style={{ background: 'transparent', border: 'none', color: 'var(--parchment)', cursor: 'pointer', fontSize: '0.9rem', borderBottom: '2px solid var(--parchment)', fontWeight: 'bold' }}
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

      <main className="container" style={{ maxWidth: '1000px' }}>
        <div className="page-title-row" style={{ marginBottom: '2rem' }}>
          <div>
            <p className="eyebrow" style={{ margin: 0 }}>Owner Portal</p>
            <h1 style={{ marginTop: '0.2rem' }}>Received Enquiries</h1>
          </div>
        </div>

        {error && <p className="form-error" role="alert" style={{ marginBottom: '1.5rem' }}>{error}</p>}

        {loading ? (
          <p style={{ fontSize: '1.2rem', fontStyle: 'italic', color: 'var(--fog)' }}>Loading received enquiries...</p>
        ) : enquiries.length === 0 ? (
          <div className="empty-state" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
            <h3 style={{ fontFamily: 'Fraunces, serif', fontSize: '1.6rem', marginBottom: '0.5rem' }}>No enquiries received yet.</h3>
            <p style={{ color: 'var(--fog)' }}>When tenants submit interest on your listings, they will show up here for you to accept or decline.</p>
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

                  {/* Right Side: Details & Accept/Decline Actions */}
                  <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                    <div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <div>
                          <h3 style={{ fontFamily: 'Fraunces, serif', fontSize: '1.25rem', margin: 0, color: 'var(--ink)' }}>{enq.property.title}</h3>
                          <p style={{ margin: '0.1rem 0 0 0', fontSize: '0.8rem', color: 'var(--fog)' }}>
                            Enquiry from <strong>{enq.tenant.name}</strong> (<a href={`mailto:${enq.tenant.email}`}>{enq.tenant.email}</a> • <a href={`tel:${enq.tenant.phone}`}>{enq.tenant.phone}</a>)
                          </p>
                        </div>
                        <span className={`badge ${
                          enq.status === 'ACCEPTED' ? 'badge-active' : enq.status === 'DECLINED' ? 'badge-rented' : 'badge-pending'
                        }`}>
                          {enq.status}
                        </span>
                      </div>
                      <p style={{ margin: '0.2rem 0', fontWeight: '600', color: 'var(--clay)' }}>₹{enq.property.rent.toLocaleString('en-IN')} / month</p>
                      
                      <div style={{ background: 'var(--parchment)', padding: '0.8rem', borderRadius: '6px', fontSize: '0.85rem', marginBottom: '0.8rem', marginTop: '0.5rem' }}>
                        <div style={{ margin: '0.2rem 0' }}><strong>Requested Move-in Date:</strong> {new Date(enq.moveInDate).toLocaleDateString('en-IN', { year: 'numeric', month: 'long', day: 'numeric' })}</div>
                        <div style={{ margin: '0.2rem 0' }}><strong>Occupants:</strong> {enq.occupants}</div>
                        {enq.message && <div style={{ margin: '0.4rem 0 0 0', fontStyle: 'italic', color: 'var(--charcoal)' }}>"{enq.message}"</div>}
                      </div>
                    </div>

                    {/* Actions block */}
                    {enq.status === 'PENDING' ? (
                      <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                        <button 
                          onClick={() => handleAccept(enq.id)}
                          disabled={actionLoading !== null}
                          style={{ flex: 1, background: 'var(--sage)' }}
                        >
                          {actionLoading === enq.id ? 'Processing...' : 'Accept & Share Contact'}
                        </button>
                        <button 
                          onClick={() => handleDecline(enq.id)}
                          disabled={actionLoading !== null}
                          className="btn-secondary"
                          style={{ flex: 1, color: '#8b2d20', borderColor: '#8b2d20' }}
                        >
                          {actionLoading === enq.id ? 'Processing...' : 'Decline'}
                        </button>
                      </div>
                    ) : enq.status === 'ACCEPTED' ? (
                      <div style={{ fontSize: '0.82rem', color: 'var(--sage)', fontWeight: 'bold', fontStyle: 'italic' }}>
                        ✓ Accepted. Your contact details (email and phone number) have been shared with this tenant.
                      </div>
                    ) : (
                      <div style={{ fontSize: '0.82rem', color: 'var(--fog)', fontStyle: 'italic' }}>
                        Declined.
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
