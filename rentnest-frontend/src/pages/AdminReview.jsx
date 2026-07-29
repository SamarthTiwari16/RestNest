import { useState, useEffect } from 'react';
import * as adminApi from '../api/adminApi.js';
import PropertyDetailsModal from '../components/property/PropertyDetailsModal.jsx';
import Header from '../components/Header.jsx';
import { useAuth } from '../hooks/useAuth.js';

export default function AdminReview({
  onGoToDashboard, onGoToListings, onGoToSearch, onGoToSaved,
  onGoToSentEnquiries, onGoToReceivedEnquiries, onGoToAdminReview
}) {
  const { user, logout } = useAuth();
  const [properties, setProperties] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Selected property for preview/details modal
  const [selectedProperty, setSelectedProperty] = useState(null);

  // Rejection panel active states
  const [rejectingId, setRejectingId] = useState(null);
  const [rejectionReason, setRejectionReason] = useState('');
  const [submittingReject, setSubmittingReject] = useState(false);

  const fetchPending = async (p = 0) => {
    try {
      setLoading(true);
      setError('');
      const { data } = await adminApi.getPendingProperties(p, 10);
      setProperties(data.content);
      setTotalPages(data.totalPages);
      setPage(p);
    } catch (err) {
      setError('Failed to fetch pending verification listings.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPending(0);
  }, []);

  const handleApprove = async (id) => {
    try {
      setError('');
      setSuccessMsg('');
      await adminApi.approveProperty(id);
      setSuccessMsg('Property listing successfully approved and set to ACTIVE.');
      fetchPending(page);
    } catch (err) {
      setError('Failed to approve property listing.');
    }
  };

  const handleOpenReject = (id) => {
    setRejectingId(id);
    setRejectionReason('');
    setError('');
    setSuccessMsg('');
  };

  const handleCancelReject = () => {
    setRejectingId(null);
    setRejectionReason('');
  };

  const handleSubmitReject = async (id) => {
    if (!rejectionReason.trim()) {
      setError('Please provide a reason for rejection.');
      return;
    }
    try {
      setSubmittingReject(true);
      setError('');
      setSuccessMsg('');
      await adminApi.rejectProperty(id, rejectionReason.trim());
      setSuccessMsg('Property listing rejected and sent back to DRAFT.');
      setRejectingId(null);
      setRejectionReason('');
      fetchPending(page);
    } catch (err) {
      setError('Failed to reject property listing.');
    } finally {
      setSubmittingReject(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', background: 'var(--parchment)' }}>
      <Header
        activeTab="ADMIN_REVIEW"
        onGoToDashboard={onGoToDashboard}
        onGoToListings={onGoToListings}
        onGoToReceivedEnquiries={onGoToReceivedEnquiries}
        onGoToSearch={onGoToSearch}
        onGoToSaved={onGoToSaved}
        onGoToSentEnquiries={onGoToSentEnquiries}
        onGoToAdminReview={onGoToAdminReview}
      />

      <main className="container" style={{ maxWidth: '1200px', paddingTop: '3rem', paddingBottom: '4rem' }}>
        {/* Page header */}
        <div style={{ marginBottom: '3rem' }}>
          <p className="eyebrow" style={{ margin: 0 }}>Moderator Workspace</p>
          <h1 style={{ marginTop: '0.25rem', marginBottom: '0.5rem' }}>Listing Review Queue</h1>
          <p style={{ color: 'var(--ink-light)', maxWidth: '520px', lineHeight: 1.6, margin: 0 }}>
            Inspect properties submitted for verification. Approve to publish active listings or reject with a reason.
          </p>
        </div>

        {error && <p className="form-error" role="alert" style={{ marginBottom: '2rem' }}>{error}</p>}
        {successMsg && <p style={{ color: 'var(--sage)', fontWeight: 600, background: 'rgba(95, 107, 78, 0.1)', padding: '1rem', borderRadius: '8px', border: '1px solid var(--sage)', marginBottom: '2rem' }}>{successMsg}</p>}

        {loading ? (
          <div style={{ textAlign: 'center', padding: '4rem 0', color: 'var(--ink-light)' }}>
            <div style={{ width: 40, height: 40, border: '3px solid var(--gold-thread)', borderTopColor: 'transparent', borderRadius: '50%', margin: '0 auto 1rem', animation: 'spin 0.8s linear infinite' }} />
            <p>Loading pending verification queue…</p>
          </div>
        ) : properties.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '4.5rem 2rem', background: 'var(--white)', borderRadius: '12px', border: '1px solid var(--border)' }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📋</div>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 600, color: 'var(--ink)' }}>All caught up!</h2>
            <p style={{ color: 'var(--ink-light)', margin: '0.5rem 0 0', fontSize: '0.9rem' }}>No listings are currently awaiting verification.</p>
          </div>
        ) : (
          <div style={{ display: 'grid', gap: '1.5rem' }}>
            {properties.map(p => {
              const coverImage = p.images && p.images.length > 0 ? p.images[0].imageUrl : null;
              const isRejectingThis = rejectingId === p.id;

              return (
                <div
                  key={p.id}
                  id={`pending-card-${p.id}`}
                  style={{
                    background: 'var(--white)',
                    border: '1px solid var(--border)',
                    borderRadius: '12px',
                    overflow: 'hidden',
                    display: 'flex',
                    flexDirection: 'column',
                    boxShadow: '0 2px 8px rgba(0,0,0,0.04)',
                  }}
                >
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: 0 }}>
                    {/* Cover Photo */}
                    <div style={{ width: '220px', minHeight: '160px', background: 'var(--parchment)', overflow: 'hidden', display: 'flex', alignItems: 'stretch' }}>
                      {coverImage ? (
                        <img src={`http://localhost:8080${coverImage}`} alt={p.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                      ) : (
                        <div style={{ width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2.5rem', background: 'var(--parchment)' }}>🏠</div>
                      )}
                    </div>

                    {/* Metadata details */}
                    <div style={{ flex: 1, minWidth: '280px', padding: '1.5rem', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                      <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '1rem' }}>
                          <div>
                            <h3 style={{ margin: 0, fontSize: '1.15rem', color: 'var(--ink)', fontWeight: 700 }}>{p.title}</h3>
                            <p style={{ margin: '0.25rem 0 0', color: 'var(--ink-light)', fontSize: '0.85rem' }}>{p.locality}, {p.city}</p>
                          </div>
                          <span style={{ fontFamily: 'var(--font-tabular)', fontWeight: 700, fontSize: '1.2rem', color: 'var(--ink)' }}>
                            ₹{Number(p.rent).toLocaleString('en-IN')}<span style={{ fontWeight: 400, color: 'var(--ink-light)', fontSize: '0.8rem' }}>/mo</span>
                          </span>
                        </div>

                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem 1.25rem', marginTop: '1rem', fontSize: '0.85rem', color: 'var(--ink)' }}>
                          <span><strong>BHK:</strong> {p.bhk}</span>
                          <span><strong>Type:</strong> {p.propertyType}</span>
                          <span><strong>Furnished:</strong> {p.furnished ? 'Yes' : 'No'}</span>
                          <span><strong>Parking:</strong> {p.parking ? 'Yes' : 'No'}</span>
                          <span><strong>Pet-Friendly:</strong> {p.petFriendly ? 'Yes' : 'No'}</span>
                        </div>

                        <div style={{ marginTop: '1rem', borderTop: '1px solid var(--border)', paddingTop: '0.85rem', fontSize: '0.85rem', color: 'var(--ink-light)' }}>
                          <strong>Owner:</strong> {p.owner.name} ({p.owner.email} | {p.owner.phone})
                        </div>
                      </div>

                      {/* Actions */}
                      <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.5rem', flexWrap: 'wrap' }}>
                        <button className="btn-secondary" style={{ padding: '0.4rem 1rem', fontSize: '0.85rem' }} onClick={() => setSelectedProperty(p)}>
                          Preview Details
                        </button>
                        <button
                          style={{ background: 'var(--sage)', color: 'var(--white)', border: 'none', borderRadius: '6px', padding: '0.4rem 1.25rem', fontWeight: 600, fontSize: '0.85rem', cursor: 'pointer' }}
                          onClick={() => handleApprove(p.id)}
                        >
                          Approve Listing
                        </button>
                        <button
                          style={{ background: 'transparent', border: '1px solid var(--clay)', color: 'var(--clay)', borderRadius: '6px', padding: '0.4rem 1.25rem', fontWeight: 600, fontSize: '0.85rem', cursor: 'pointer' }}
                          onClick={() => handleOpenReject(p.id)}
                        >
                          Reject
                        </button>
                      </div>
                    </div>
                  </div>

                  {/* Reject panel details */}
                  {isRejectingThis && (
                    <div style={{ padding: '1.5rem', background: '#FFF9F6', borderTop: '1px solid #FFECE5' }}>
                      <p style={{ margin: '0 0 0.5rem', fontWeight: 600, color: 'var(--clay)', fontSize: '0.875rem' }}>Provide Reason for Rejection:</p>
                      <textarea
                        rows={3}
                        value={rejectionReason}
                        onChange={(e) => setRejectionReason(e.target.value)}
                        placeholder="e.g. Please provide photos of all bedrooms. Current pictures are blurred."
                        style={{
                          width: '100%',
                          padding: '0.75rem',
                          borderRadius: '8px',
                          border: '1px solid #FFD4C2',
                          fontFamily: 'inherit',
                          fontSize: '0.9rem',
                          outline: 'none',
                          marginBottom: '0.75rem',
                          resize: 'vertical',
                        }}
                      />
                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button
                          style={{ background: 'var(--clay)', color: 'var(--white)', border: 'none', borderRadius: '6px', padding: '0.4rem 1.25rem', fontWeight: 600, fontSize: '0.85rem', cursor: 'pointer' }}
                          disabled={submittingReject}
                          onClick={() => handleSubmitReject(p.id)}
                        >
                          {submittingReject ? 'Submitting...' : 'Submit Rejection'}
                        </button>
                        <button
                          className="btn-secondary"
                          style={{ padding: '0.4rem 1rem', fontSize: '0.85rem', borderColor: 'var(--border)' }}
                          onClick={handleCancelReject}
                        >
                          Cancel
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div style={{ display: 'flex', justifyContent: 'center', gap: '0.5rem', marginTop: '2.5rem' }}>
            <button
              disabled={page === 0}
              onClick={() => fetchPending(page - 1)}
              style={{ padding: '0.4rem 0.75rem', background: 'transparent', border: '1px solid var(--border)', borderRadius: '6px', cursor: page === 0 ? 'not-allowed' : 'pointer', opacity: page === 0 ? 0.5 : 1 }}
            >
              Previous
            </button>
            <span style={{ alignSelf: 'center', fontSize: '0.9rem', color: 'var(--ink-light)' }}>Page {page + 1} of {totalPages}</span>
            <button
              disabled={page === totalPages - 1}
              onClick={() => fetchPending(page + 1)}
              style={{ padding: '0.4rem 0.75rem', background: 'transparent', border: '1px solid var(--border)', borderRadius: '6px', cursor: page === totalPages - 1 ? 'not-allowed' : 'pointer', opacity: page === totalPages - 1 ? 0.5 : 1 }}
            >
              Next
            </button>
          </div>
        )}
      </main>

      {/* Property Details Modal */}
      {selectedProperty && (
        <PropertyDetailsModal
          property={selectedProperty}
          onClose={() => setSelectedProperty(null)}
        />
      )}

      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
      `}</style>
    </div>
  );
}
