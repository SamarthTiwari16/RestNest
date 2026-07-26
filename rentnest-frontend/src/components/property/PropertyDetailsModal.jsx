import { useState, useEffect } from 'react';
import * as enquiriesApi from '../../api/enquiriesApi.js';
import { useAuth } from '../../hooks/useAuth.js';

export default function PropertyDetailsModal({ property, onClose, onEnquirySuccess }) {
  const { user } = useAuth();
  
  // Image Carousel State
  const [currentImgIndex, setCurrentImgIndex] = useState(0);
  const images = property.images || [];

  // Form State
  const [message, setMessage] = useState('');
  const [moveInDate, setMoveInDate] = useState('');
  const [occupants, setOccupants] = useState(1);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  // Enquiry Check
  const [alreadyEnquired, setAlreadyEnquired] = useState(false);
  const [enquiryStatus, setEnquiryStatus] = useState('');
  const [revealedOwnerContact, setRevealedOwnerContact] = useState(null);

  const fetchEnquiryStatus = async () => {
    try {
      const { data } = await enquiriesApi.getSentEnquiries();
      const existing = data.find(e => e.property.id === property.id);
      if (existing) {
        setAlreadyEnquired(true);
        setEnquiryStatus(existing.status);
        if (existing.status === 'ACCEPTED') {
          setRevealedOwnerContact(existing.owner);
        }
      }
    } catch (err) {
      console.error('Failed to load enquiries status:', err);
    }
  };

  useEffect(() => {
    fetchEnquiryStatus();
  }, [property.id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!moveInDate) {
      setError('Please select a move-in date');
      return;
    }
    if (occupants < 1) {
      setError('Number of occupants must be at least 1');
      return;
    }

    try {
      setLoading(true);
      setError('');
      const payload = { message, moveInDate, occupants };
      await enquiriesApi.sendEnquiry(property.id, payload);
      setSuccess(true);
      if (onEnquirySuccess) onEnquirySuccess();
      await fetchEnquiryStatus();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit enquiry. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleNextImage = () => {
    if (images.length > 0) {
      setCurrentImgIndex((prev) => (prev + 1) % images.length);
    }
  };

  const handlePrevImage = () => {
    if (images.length > 0) {
      setCurrentImgIndex((prev) => (prev - 1 + images.length) % images.length);
    }
  };

  const isOwner = property.ownerId === user.id || property.owner?.id === user.id;

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(21, 19, 15, 0.75)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000,
      padding: '1.5rem',
      backdropFilter: 'blur(4px)'
    }}>
      <div className="form-card" style={{
        maxWidth: '850px',
        width: '100%',
        maxHeight: '90vh',
        overflowY: 'auto',
        borderRadius: '12px',
        padding: '2rem',
        margin: 0,
        position: 'relative'
      }}>
        {/* Close Button */}
        <button 
          onClick={onClose} 
          style={{
            position: 'absolute',
            top: '1rem',
            right: '1rem',
            background: 'transparent',
            color: 'var(--charcoal)',
            fontSize: '1.5rem',
            border: 'none',
            cursor: 'pointer',
            padding: '0.5rem',
            lineHeight: 1
          }}
        >
          &times;
        </button>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', marginTop: '1rem' }}>
          
          {/* Left Column: Media & Details */}
          <div>
            {images.length > 0 ? (
              <div style={{ position: 'relative', width: '100%', height: '240px', overflow: 'hidden', borderRadius: '8px', backgroundColor: 'var(--ink)' }}>
                <img 
                  src={images[currentImgIndex].imageUrl.startsWith('/') ? `http://localhost:8080${images[currentImgIndex].imageUrl}` : images[currentImgIndex].imageUrl} 
                  alt={`${property.title} preview`} 
                  style={{ width: '100%', height: '100%', objectFit: 'cover' }} 
                />
                {images.length > 1 && (
                  <>
                    <button 
                      onClick={handlePrevImage}
                      style={{ position: 'absolute', left: '0.5rem', top: '50%', transform: 'translateY(-50%)', background: 'rgba(21,19,15,0.6)', border: 'none', borderRadius: '50%', width: '2rem', height: '2rem', padding: 0, color: 'var(--parchment)', fontSize: '1rem' }}
                    >
                      ◀
                    </button>
                    <button 
                      onClick={handleNextImage}
                      style={{ position: 'absolute', right: '0.5rem', top: '50%', transform: 'translateY(-50%)', background: 'rgba(21,19,15,0.6)', border: 'none', borderRadius: '50%', width: '2rem', height: '2rem', padding: 0, color: 'var(--parchment)', fontSize: '1rem' }}
                    >
                      ▶
                    </button>
                    <div style={{ position: 'absolute', bottom: '0.5rem', left: '50%', transform: 'translateX(-50%)', color: 'var(--parchment)', fontSize: '0.75rem', background: 'rgba(21,19,15,0.6)', padding: '0.2rem 0.5rem', borderRadius: '10px' }}>
                      {currentImgIndex + 1} / {images.length}
                    </div>
                  </>
                )}
              </div>
            ) : (
              <div style={{ width: '100%', height: '240px', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: 'var(--clay)', borderRadius: '8px', color: 'var(--fog)', fontStyle: 'italic', fontSize: '0.9rem' }}>
                No photos available
              </div>
            )}

            <h2 style={{ fontFamily: 'Fraunces, serif', fontSize: '1.8rem', marginTop: '1.5rem', marginBottom: '0.5rem', color: 'var(--ink)' }}>
              {property.title}
            </h2>
            <p style={{ margin: '0.2rem 0', fontWeight: '600', color: 'var(--clay)', fontSize: '1.4rem' }}>
              ₹{property.rent.toLocaleString('en-IN')} / month
            </p>
            <p style={{ margin: '0.4rem 0', color: 'var(--charcoal)', fontWeight: 500 }}>
              {property.locality}, {property.city}
            </p>

            <hr style={{ border: '0', borderTop: '1px solid rgb(138 130 114 / 25%)', margin: '1rem 0' }} />

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.8rem', fontSize: '0.9rem', color: 'var(--charcoal)' }}>
              <div><strong>BHK:</strong> {property.bhk} BHK</div>
              <div><strong>Property Type:</strong> {property.propertyType.replace('_', ' ')}</div>
              <div><strong>Furnishing:</strong> {property.furnished ? 'Fully Furnished' : 'Unfurnished'}</div>
              <div><strong>Parking:</strong> {property.parking ? 'Available' : 'None'}</div>
              <div><strong>Pet Friendly:</strong> {property.petFriendly ? 'Yes' : 'No'}</div>
              <div><strong>Available From:</strong> {new Date(property.availableFrom).toLocaleDateString('en-IN', { year: 'numeric', month: 'long', day: 'numeric' })}</div>
            </div>
          </div>

          {/* Right Column: Privacy Disclosures & Enquiry Form */}
          <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <div>
              <h3 style={{ fontFamily: 'Fraunces, serif', fontSize: '1.4rem', margin: '0 0 1rem 0' }}>Contact & Enquiry</h3>
              
              {isOwner ? (
                <div className="empty-state" style={{ padding: '1.5rem', margin: 0, textAlign: 'center' }}>
                  <p style={{ fontSize: '0.95rem', color: 'var(--fog)', margin: 0 }}>This is your property listing. You cannot submit enquiries to your own posts.</p>
                </div>
              ) : alreadyEnquired ? (
                <div style={{ padding: '1.5rem', background: 'var(--paper-white)', border: '1px solid rgb(138 130 114 / 25%)', borderRadius: '8px' }}>
                  <p style={{ fontWeight: '600', margin: '0 0 0.8rem 0' }}>
                    Enquiry Status:{' '}
                    <span className={`badge ${
                      enquiryStatus === 'ACCEPTED' ? 'badge-active' : enquiryStatus === 'DECLINED' ? 'badge-rented' : 'badge-pending'
                    }`}>
                      {enquiryStatus}
                    </span>
                  </p>
                  
                  {enquiryStatus === 'ACCEPTED' && revealedOwnerContact ? (
                    <div style={{ marginTop: '1rem', borderTop: '1px solid rgb(138 130 114 / 25%)', paddingTop: '1rem' }}>
                      <p style={{ color: 'var(--sage)', fontWeight: 'bold', margin: '0 0 0.5rem 0' }}>✓ Contact Released by Owner</p>
                      <p style={{ margin: '0.2rem 0' }}><strong>Owner:</strong> {revealedOwnerContact.name}</p>
                      <p style={{ margin: '0.2rem 0' }}><strong>Email:</strong> <a href={`mailto:${revealedOwnerContact.email}`}>{revealedOwnerContact.email}</a></p>
                      <p style={{ margin: '0.2rem 0' }}><strong>Phone:</strong> <a href={`tel:${revealedOwnerContact.phone}`}>{revealedOwnerContact.phone}</a></p>
                    </div>
                  ) : enquiryStatus === 'DECLINED' ? (
                    <p style={{ color: 'var(--clay)', margin: 0, fontSize: '0.9rem' }}>The owner has declined this enquiry. Contact details are not available.</p>
                  ) : (
                    <p style={{ color: 'var(--fog)', margin: 0, fontSize: '0.9rem', fontStyle: 'italic' }}>
                      Your request is pending owner approval. Contact information is hidden until they accept.
                    </p>
                  )}
                </div>
              ) : (
                <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '1rem' }}>
                  <p style={{ color: 'var(--fog)', fontSize: '0.82rem', lineHeight: '1.4', margin: '0 0 0.5rem 0' }}>
                    ℹ Owner contact information is kept private. Submit your occupancy details below to request contact disclosure.
                  </p>

                  {error && <p className="form-error" role="alert">{error}</p>}
                  {success && <p style={{ color: 'var(--sage)', fontWeight: 'bold', margin: 0 }}>✓ Enquiry submitted successfully!</p>}

                  <label>
                    Introduce Yourself / Move-in Message
                    <textarea 
                      rows="3"
                      placeholder="e.g. Hi Ramesh, I am a software engineer looking for a house near Indiranagar. I don't have pets..."
                      value={message}
                      onChange={(e) => setMessage(e.target.value)}
                      style={{ 
                        width: '100%',
                        padding: '0.72rem',
                        border: '1px solid rgb(138 130 114 / 55%)',
                        borderRadius: '6px',
                        background: 'var(--paper-white)',
                        fontFamily: 'inherit',
                        resize: 'vertical'
                      }}
                    />
                  </label>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.8rem' }}>
                    <label>
                      Move-in Date
                      <input 
                        type="date"
                        value={moveInDate}
                        min={new Date().toISOString().split('T')[0]}
                        onChange={(e) => setMoveInDate(e.target.value)}
                      />
                    </label>
                    <label>
                      Number of Occupants
                      <input 
                        type="number"
                        min="1"
                        value={occupants}
                        onChange={(e) => setOccupants(parseInt(e.target.value) || 1)}
                      />
                    </label>
                  </div>

                  <button type="submit" disabled={loading} style={{ width: '100%', marginTop: '0.5rem' }}>
                    {loading ? 'Submitting...' : 'Send Enquiry Request'}
                  </button>
                </form>
              )}
            </div>

            <button 
              className="btn-secondary" 
              onClick={onClose} 
              style={{ width: '100%', marginTop: '2rem' }}
            >
              Back to Explorer
            </button>
          </div>

        </div>

      </div>
    </div>
  );
}
