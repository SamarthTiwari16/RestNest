import { useState, useEffect } from 'react';
import * as dashboardApi from '../api/dashboardApi.js';
import { useAuth } from '../hooks/useAuth.js';

export default function Dashboard({
  onGoToDashboard, onGoToListings, onGoToSearch, onGoToSaved,
  onGoToSentEnquiries, onGoToReceivedEnquiries, onGoToAdminReview
}) {
  const { user, logout } = useAuth();
  const [ownerStats, setOwnerStats] = useState(null);
  const [tenantStats, setTenantStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAll = async () => {
      try {
        setLoading(true);
        const [ownerRes, tenantRes] = await Promise.all([
          dashboardApi.getOwnerDashboard(),
          dashboardApi.getTenantDashboard(),
        ]);
        setOwnerStats(ownerRes.data);
        setTenantStats(tenantRes.data);
      } catch (err) {
        setError('Failed to load your dashboard. Please try again.');
      } finally {
        setLoading(false);
      }
    };
    fetchAll();
  }, []);

  const navBtnStyle = {
    background: 'transparent', border: 'none', color: 'var(--parchment)',
    cursor: 'pointer', fontSize: '0.9rem', padding: '0.25rem 0',
  };
  const navBtnActive = { ...navBtnStyle, borderBottom: '2px solid var(--parchment)', fontWeight: 700 };

  return (
    <div style={{ minHeight: '100vh', background: 'var(--parchment)' }}>
      {/* Navbar */}
      <header className="navbar">
        <h1>RentNest</h1>
        <div className="nav-links" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <button style={navBtnActive} onClick={onGoToDashboard}>Dashboard</button>
          <button style={navBtnStyle} onClick={onGoToListings}>My Listings</button>
          <button style={navBtnStyle} onClick={onGoToReceivedEnquiries}>Received Enquiries</button>
          <button style={navBtnStyle} onClick={onGoToSearch}>Search Properties</button>
          <button style={navBtnStyle} onClick={onGoToSaved}>Saved Properties</button>
          <button style={navBtnStyle} onClick={onGoToSentEnquiries}>My Enquiries</button>
          {user.role === 'ROLE_ADMIN' && (
            <button style={navBtnStyle} onClick={onGoToAdminReview}>Admin Review</button>
          )}
          <span className="nav-link" style={{ marginLeft: '1rem' }}>Hello, {user.name}</span>
          <button className="btn-secondary" style={{ color: 'var(--parchment)', borderColor: 'var(--parchment)' }} onClick={logout}>Sign out</button>
        </div>
      </header>

      <main className="container" style={{ maxWidth: '1200px', paddingTop: '3rem', paddingBottom: '4rem' }}>
        {/* Page header */}
        <div style={{ marginBottom: '3rem' }}>
          <p className="eyebrow" style={{ margin: 0 }}>Your Overview</p>
          <h1 style={{ marginTop: '0.25rem', marginBottom: '0.5rem' }}>Dashboard</h1>
          <p style={{ color: 'var(--ink-light)', maxWidth: '520px', lineHeight: 1.6, margin: 0 }}>
            A snapshot of your activity across listings, enquiries, and saved properties.
          </p>
        </div>

        {error && <p className="form-error" role="alert" style={{ marginBottom: '2rem' }}>{error}</p>}

        {loading ? (
          <div style={{ textAlign: 'center', padding: '4rem 0', color: 'var(--ink-light)' }}>
            <div style={{ width: 40, height: 40, border: '3px solid var(--gold-thread)', borderTopColor: 'transparent', borderRadius: '50%', margin: '0 auto 1rem', animation: 'spin 0.8s linear infinite' }} />
            <p>Loading your dashboard…</p>
          </div>
        ) : (
          <>
            {/* ─── Owner Stats ─── */}
            <section style={{ marginBottom: '3.5rem' }}>
              <h2 style={{ fontSize: '1.1rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink)', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <span>Owner Activity</span>
                <span style={{ flex: 1, height: 1, background: 'var(--gold-thread)', opacity: 0.5 }} />
              </h2>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', gap: '1.5rem' }}>
                <StatCard label="Total Properties" value={ownerStats?.totalProperties ?? 0} onClick={onGoToListings} accent />
                <StatCard label="Active Listings" value={ownerStats?.activeListings ?? 0} onClick={onGoToListings} />
                <StatCard label="Rented Out" value={ownerStats?.rentedCount ?? 0} onClick={onGoToListings} />
                <StatCard label="Enquiries Received" value={ownerStats?.totalEnquiriesReceived ?? 0} onClick={onGoToReceivedEnquiries} />
              </div>
            </section>

            {/* ─── Tenant Stats ─── */}
            <section style={{ marginBottom: '3.5rem' }}>
              <h2 style={{ fontSize: '1.1rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink)', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <span>Tenant Activity</span>
                <span style={{ flex: 1, height: 1, background: 'var(--gold-thread)', opacity: 0.5 }} />
              </h2>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', gap: '1.5rem' }}>
                <StatCard label="Saved Properties" value={tenantStats?.savedPropertiesCount ?? 0} onClick={onGoToSaved} accent />
                <StatCard label="Pending Enquiries" value={tenantStats?.pendingEnquiriesCount ?? 0} onClick={onGoToSentEnquiries} />
                <StatCard label="Accepted Enquiries" value={tenantStats?.acceptedEnquiriesCount ?? 0} onClick={onGoToSentEnquiries} />
                <StatCard label="Declined Enquiries" value={tenantStats?.declinedEnquiriesCount ?? 0} onClick={onGoToSentEnquiries} />
              </div>
            </section>

            {/* ─── Recently Viewed ─── */}
            {tenantStats?.recentlyViewed && tenantStats.recentlyViewed.length > 0 && (
              <section>
                <h2 style={{ fontSize: '1.1rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink)', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                  <span>Recently Viewed</span>
                  <span style={{ flex: 1, height: 1, background: 'var(--gold-thread)', opacity: 0.5 }} />
                </h2>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1.25rem' }}>
                  {tenantStats.recentlyViewed.map(p => (
                    <RecentPropertyCard key={p.id} property={p} onClick={onGoToSearch} />
                  ))}
                </div>
              </section>
            )}

            {/* ─── Quick Actions ─── */}
            <section style={{ marginTop: '3.5rem', padding: '2rem', background: 'var(--white)', borderRadius: '12px', border: '1px solid var(--border)', display: 'flex', flexWrap: 'wrap', gap: '1rem', alignItems: 'center' }}>
              <div style={{ flex: 1, minWidth: '180px' }}>
                <p style={{ margin: 0, fontWeight: 700, color: 'var(--ink)', fontSize: '1rem' }}>Quick Navigation</p>
                <p style={{ margin: '0.25rem 0 0', color: 'var(--ink-light)', fontSize: '0.875rem' }}>Jump straight to any section</p>
              </div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem' }}>
                <QuickBtn label="Search Properties" onClick={onGoToSearch} primary />
                <QuickBtn label="My Listings" onClick={onGoToListings} />
                <QuickBtn label="Received Enquiries" onClick={onGoToReceivedEnquiries} />
                <QuickBtn label="My Enquiries" onClick={onGoToSentEnquiries} />
                <QuickBtn label="Saved Properties" onClick={onGoToSaved} />
              </div>
            </section>
          </>
        )}
      </main>

      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
      `}</style>
    </div>
  );
}

function StatCard({ label, value, onClick, accent }) {
  const [hovered, setHovered] = useState(false);
  return (
    <div
      id={`stat-card-${label.replace(/\s+/g, '-').toLowerCase()}`}
      onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        background: 'var(--white)',
        border: '1px solid var(--border)',
        borderTop: accent ? '3px solid var(--gold-thread)' : '3px solid transparent',
        borderRadius: '10px',
        padding: '1.75rem 1.5rem',
        cursor: onClick ? 'pointer' : 'default',
        transition: 'box-shadow 0.2s, transform 0.15s',
        boxShadow: hovered ? '0 6px 24px rgba(0,0,0,0.09)' : '0 2px 8px rgba(0,0,0,0.04)',
        transform: hovered ? 'translateY(-2px)' : 'none',
      }}
    >
      <p style={{
        fontFamily: "'Inter', sans-serif",
        fontVariantNumeric: 'tabular-nums',
        fontSize: '2.75rem',
        fontWeight: 700,
        color: 'var(--ink)',
        margin: 0,
        lineHeight: 1,
      }}>
        {value}
      </p>
      <p style={{ margin: '0.5rem 0 0', fontSize: '0.85rem', color: 'var(--ink-light)', fontWeight: 500, letterSpacing: '0.02em' }}>
        {label}
      </p>
    </div>
  );
}

function RecentPropertyCard({ property, onClick }) {
  const [hovered, setHovered] = useState(false);
  const coverImage = property.imageUrls && property.imageUrls.length > 0 ? property.imageUrls[0] : null;

  return (
    <div
      onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        background: 'var(--white)',
        border: '1px solid var(--border)',
        borderRadius: '10px',
        overflow: 'hidden',
        cursor: 'pointer',
        transition: 'box-shadow 0.2s, transform 0.15s',
        boxShadow: hovered ? '0 6px 24px rgba(0,0,0,0.09)' : '0 2px 8px rgba(0,0,0,0.04)',
        transform: hovered ? 'translateY(-2px)' : 'none',
        display: 'flex',
        alignItems: 'stretch',
        gap: 0,
      }}
    >
      {/* Thumbnail */}
      <div style={{ width: 90, minHeight: 80, flexShrink: 0, background: 'var(--parchment)', overflow: 'hidden' }}>
        {coverImage ? (
          <img src={`http://localhost:8080${coverImage}`} alt={property.title}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
        ) : (
          <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem' }}>🏠</div>
        )}
      </div>

      {/* Info */}
      <div style={{ padding: '0.85rem 1rem', flex: 1, minWidth: 0 }}>
        <p style={{ margin: 0, fontWeight: 600, color: 'var(--ink)', fontSize: '0.92rem', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{property.title}</p>
        <p style={{ margin: '0.2rem 0 0', color: 'var(--ink-light)', fontSize: '0.78rem' }}>{property.locality}, {property.city}</p>
        <p style={{ margin: '0.5rem 0 0', fontWeight: 700, color: 'var(--ink)', fontSize: '0.9rem', fontVariantNumeric: 'tabular-nums' }}>
          ₹{Number(property.rent).toLocaleString('en-IN')}<span style={{ fontWeight: 400, color: 'var(--ink-light)', fontSize: '0.75rem' }}>/mo</span>
        </p>
      </div>
    </div>
  );
}

function QuickBtn({ label, onClick, primary }) {
  const [hovered, setHovered] = useState(false);
  return (
    <button
      onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        padding: '0.5rem 1.25rem',
        borderRadius: '8px',
        fontSize: '0.875rem',
        fontWeight: 600,
        cursor: 'pointer',
        transition: 'all 0.15s',
        background: primary
          ? (hovered ? 'var(--ink)' : 'var(--gold-thread)')
          : (hovered ? 'var(--parchment)' : 'transparent'),
        color: primary ? (hovered ? 'var(--parchment)' : 'var(--ink)') : 'var(--ink)',
        border: primary ? 'none' : '1px solid var(--border)',
      }}
    >
      {label}
    </button>
  );
}
