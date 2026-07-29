import { useState, useEffect } from 'react';
import * as propertyApi from '../api/propertyApi.js';
import * as favouritesApi from '../api/favouritesApi.js';
import PropertyDetailsModal from '../components/property/PropertyDetailsModal.jsx';
import Header from '../components/Header.jsx';
import { useAuth } from '../hooks/useAuth.js';

export default function Search({ onGoToDashboard, onGoToListings, onGoToSearch, onGoToSaved, onGoToSentEnquiries, onGoToReceivedEnquiries, onGoToAdminReview }) {
  const { user, logout } = useAuth();
  const [filters, setFilters] = useState({
    city: 'Bangalore', // Default to Bangalore to show properties
    locality: '',
    minRent: '',
    maxRent: '',
    bhk: '',
    propertyType: '',
    furnished: '',
    parking: '',
    petFriendly: '',
    availableFrom: '',
  });

  const [results, setResults] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedProperty, setSelectedProperty] = useState(null);

  const handleSearch = (e) => {
    if (e) e.preventDefault();
    fetchResults(0);
  };

  const handleClear = () => {
    setFilters({
      city: '',
      locality: '',
      minRent: '',
      maxRent: '',
      bhk: '',
      propertyType: '',
      furnished: '',
      parking: '',
      petFriendly: '',
      availableFrom: '',
    });
    setResults([]);
    setTotalPages(0);
    setTotalElements(0);
    setPage(0);
  };

  const fetchResults = async (targetPage = 0) => {
    try {
      setLoading(true);
      setError('');
      
      // Clean filters to convert checkbox strings to boolean objects, etc.
      const cleanedFilters = { ...filters };
      if (cleanedFilters.furnished === 'true') cleanedFilters.furnished = true;
      else if (cleanedFilters.furnished === 'false') cleanedFilters.furnished = false;
      else delete cleanedFilters.furnished;

      if (cleanedFilters.parking === 'true') cleanedFilters.parking = true;
      else if (cleanedFilters.parking === 'false') cleanedFilters.parking = false;
      else delete cleanedFilters.parking;

      if (cleanedFilters.petFriendly === 'true') cleanedFilters.petFriendly = true;
      else if (cleanedFilters.petFriendly === 'false') cleanedFilters.petFriendly = false;
      else delete cleanedFilters.petFriendly;

      const { data } = await propertyApi.searchProperties(cleanedFilters, targetPage, 6);
      setResults(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
      setPage(targetPage);
    } catch (err) {
      setError('Failed to fetch search results. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const [favouriteIds, setFavouriteIds] = useState(new Set());

  const fetchFavouriteIds = async () => {
    try {
      const { data } = await favouritesApi.getMyFavouritePropertyIds();
      setFavouriteIds(new Set(data));
    } catch (err) {
      console.error('Failed to load saved IDs:', err);
    }
  };

  const toggleFavourite = async (propertyId) => {
    const isFavourited = favouriteIds.has(propertyId);
    setFavouriteIds(prev => {
      const next = new Set(prev);
      if (isFavourited) next.delete(propertyId);
      else next.add(propertyId);
      return next;
    });

    try {
      if (isFavourited) {
        await favouritesApi.removeFavourite(propertyId);
      } else {
        await favouritesApi.addFavourite(propertyId);
      }
    } catch (err) {
      setFavouriteIds(prev => {
        const next = new Set(prev);
        if (isFavourited) next.add(propertyId);
        else next.delete(propertyId);
        return next;
      });
    }
  };

  // Initial load
  useEffect(() => {
    fetchResults(0);
    fetchFavouriteIds();
  }, []);

  return (
    <div>
      <Header
        activeTab="SEARCH"
        onGoToDashboard={onGoToDashboard}
        onGoToListings={onGoToListings}
        onGoToReceivedEnquiries={onGoToReceivedEnquiries}
        onGoToSearch={onGoToSearch}
        onGoToSaved={onGoToSaved}
        onGoToSentEnquiries={onGoToSentEnquiries}
        onGoToAdminReview={onGoToAdminReview}
      />

      {/* Editorial Ink Hero */}
      <section style={{ background: 'var(--ink)', color: 'var(--parchment)', padding: '4.5rem 2rem 5.5rem', position: 'relative', overflow: 'hidden' }}>
        <div style={{ maxWidth: '1120px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '2rem', position: 'relative', zIndex: 2 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '2rem' }}>
            <div style={{ flex: '1 1 600px' }}>
              <p className="eyebrow" style={{ color: 'var(--gold-thread)', margin: 0 }}>Tenant Portal</p>
              <h2 className="text-hero" style={{ margin: '0.5rem 0 1.5rem', color: 'var(--parchment)' }}>
                Every rental is the start of a chapter.
              </h2>
              <p className="text-body" style={{ color: 'var(--parchment)', opacity: 0.8, maxWidth: '480px', margin: 0 }}>
                A premium, privacy-first portal matching high-quality listings with verified tenants. Discover properties in India's top cities.
              </p>
            </div>
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', flex: '1 1 200px' }}>
              <svg className="key-motif" width="140" height="140" viewBox="0 0 100 100" fill="none" stroke="var(--gold-thread)" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" style={{ overflow: 'visible' }}>
                <path d="M 35 50 A 15 15 0 1 1 35 50.01 M 35 50 A 5 5 0 1 1 35 50.01 M 50 50 L 85 50 M 70 50 L 70 65 M 80 50 L 80 65" />
              </svg>
            </div>
          </div>

          {/* Quick city search bar low in hero */}
          <div style={{ marginTop: '2.5rem', maxWidth: '560px' }}>
            <form onSubmit={handleSearch} style={{ display: 'flex', gap: '0.75rem' }}>
              <div style={{ flex: 1, position: 'relative' }}>
                <input
                  type="text"
                  placeholder="Enter city to explore (e.g. Bangalore, Chennai)..."
                  value={filters.city}
                  onChange={(e) => setFormFilter('city', e.target.value)}
                  style={{
                    background: 'var(--paper-white)',
                    border: '1px solid var(--gold-thread)',
                    borderRadius: '8px',
                    color: 'var(--ink)',
                    padding: '0.9rem 1.2rem',
                    fontSize: '1rem',
                    boxShadow: '0 0 16px rgba(201, 162, 74, 0.15)',
                    width: '100%',
                    outline: 'none'
                  }}
                />
              </div>
              <button type="submit" style={{ padding: '0 1.75rem', height: '48px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '8px' }}>
                Explore
              </button>
            </form>
          </div>
        </div>

        {/* Ambient bottom gradient cut */}
        <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0, height: '30px', background: 'linear-gradient(to bottom, transparent, var(--parchment))' }} />
      </section>

      <main className="container" style={{ maxWidth: '1120px', paddingTop: '2.5rem' }}>
        {error && <p className="form-error" role="alert" style={{ marginBottom: '1.5rem' }}>{error}</p>}

        <div style={{ display: 'grid', gridTemplateColumns: '280px 1fr', gap: '2.5rem', alignItems: 'start' }}>
          
          {/* Filters Sidebar */}
          <aside className="form-card" style={{ padding: '1.5rem', margin: 0, position: 'sticky', top: '2rem' }}>
            <h3 style={{ margin: '0 0 1.2rem 0', fontFamily: 'Fraunces, serif', fontSize: '1.4rem' }}>Filters</h3>
            <form onSubmit={handleSearch} style={{ display: 'grid', gap: '1rem' }}>
              <label>
                City
                <input 
                  placeholder="e.g. Bangalore" 
                  value={filters.city} 
                  onChange={(e) => setFormFilter('city', e.target.value)}
                />
              </label>

              <label>
                Locality
                <input 
                  placeholder="e.g. Indiranagar" 
                  value={filters.locality} 
                  onChange={(e) => setFormFilter('locality', e.target.value)}
                />
              </label>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
                <label>
                  Min Rent (₹)
                  <input 
                    type="number"
                    placeholder="Min" 
                    value={filters.minRent} 
                    onChange={(e) => setFormFilter('minRent', e.target.value)}
                  />
                </label>
                <label>
                  Max Rent (₹)
                  <input 
                    type="number"
                    placeholder="Max" 
                    value={filters.maxRent} 
                    onChange={(e) => setFormFilter('maxRent', e.target.value)}
                  />
                </label>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
                <label>
                  BHK
                  <select value={filters.bhk} onChange={(e) => setFormFilter('bhk', e.target.value)}>
                    <option value="">Any BHK</option>
                    <option value="1">1 BHK</option>
                    <option value="2">2 BHK</option>
                    <option value="3">3 BHK</option>
                    <option value="4">4+ BHK</option>
                  </select>
                </label>
                <label>
                  Type
                  <select value={filters.propertyType} onChange={(e) => setFormFilter('propertyType', e.target.value)}>
                    <option value="">Any Type</option>
                    <option value="APARTMENT">Apartment</option>
                    <option value="INDEPENDENT_HOUSE">House</option>
                    <option value="VILLA">Villa</option>
                    <option value="STUDIO">Studio</option>
                  </select>
                </label>
              </div>

              <label>
                Furnishing
                <select value={filters.furnished} onChange={(e) => setFormFilter('furnished', e.target.value)}>
                  <option value="">Any</option>
                  <option value="true">Fully Furnished</option>
                  <option value="false">Unfurnished</option>
                </select>
              </label>

              <label>
                Available From
                <input 
                  type="date" 
                  value={filters.availableFrom} 
                  onChange={(e) => setFormFilter('availableFrom', e.target.value)}
                />
              </label>

              <div style={{ display: 'grid', gap: '0.5rem', margin: '0.2rem 0' }}>
                <label className="checkbox-label" style={{ fontSize: '0.85rem' }}>
                  <input 
                    type="checkbox" 
                    checked={filters.parking === 'true'} 
                    onChange={(e) => setFormFilter('parking', e.target.checked ? 'true' : '')} 
                  />
                  Has Parking
                </label>
                <label className="checkbox-label" style={{ fontSize: '0.85rem' }}>
                  <input 
                    type="checkbox" 
                    checked={filters.petFriendly === 'true'} 
                    onChange={(e) => setFormFilter('petFriendly', e.target.checked ? 'true' : '')} 
                  />
                  Pet Friendly
                </label>
              </div>

              <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                <button type="submit" style={{ flex: 2 }}>Apply</button>
                <button type="button" className="btn-secondary" onClick={handleClear} style={{ flex: 1 }}>Clear</button>
              </div>
            </form>
          </aside>

          {/* Results Area */}
          <div>
            {loading ? (
              <p style={{ fontSize: '1.2rem', fontStyle: 'italic', color: 'var(--fog)' }}>Searching for properties...</p>
            ) : results.length === 0 ? (
              <div className="empty-state" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
                <svg width="120" height="120" viewBox="0 0 100 100" fill="none" stroke="var(--gold-thread)" strokeWidth="1.2" opacity="0.6" style={{ margin: '0 auto 1.5rem', display: 'block' }}>
                  <rect x="10" y="10" width="80" height="80" />
                  <line x1="10" y1="50" x2="60" y2="50" />
                  <line x1="50" y1="10" x2="50" y2="90" />
                  <line x1="50" y1="65" x2="90" y2="65" />
                  <path d="M 50 35 A 15 15 0 0 1 35 50" strokeDasharray="3,3" />
                  <path d="M 65 50 A 15 15 0 0 1 50 65" strokeDasharray="3,3" />
                </svg>
                <h3 style={{ fontFamily: 'Fraunces, serif', fontSize: '1.5rem', marginBottom: '0.5rem' }}>No homes match your search filters.</h3>
                <p style={{ color: 'var(--fog)' }}>Try expanding your city, locality, or budget limits.</p>
              </div>
            ) : (
              <div>
                <p style={{ margin: '0 0 1rem 0', fontWeight: '500', color: 'var(--fog)', fontSize: '0.9rem' }}>
                  Found {totalElements} properties matching your parameters
                </p>

                <div className="listing-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))' }}>
                  {results.map((item) => {
                    const coverImage = item.images && item.images.length > 0 ? item.images[0].imageUrl : null;
                    const fullCoverUrl = coverImage ? (coverImage.startsWith('/') ? `http://localhost:8080${coverImage}` : coverImage) : null;
                    return (
                      <div key={item.id} className="listing-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between', position: 'relative' }}>
                        
                        {/* Favourites Heart Toggle Overlay */}
                        <button
                          onClick={() => toggleFavourite(item.id)}
                          aria-label={favouriteIds.has(item.id) ? "Remove from saved properties" : "Save property"}
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
                          <svg width="20" height="20" viewBox="0 0 24 24" fill={favouriteIds.has(item.id) ? "var(--clay)" : "none"} stroke="var(--clay)" strokeWidth="2">
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

                {/* Pagination Navigation */}
                {totalPages > 1 && (
                  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '1rem', marginTop: '2rem' }}>
                    <button 
                      className="btn-secondary" 
                      disabled={page === 0} 
                      onClick={() => fetchResults(page - 1)}
                    >
                      ◀ Previous
                    </button>
                    <span style={{ fontSize: '0.9rem', color: 'var(--charcoal)' }}>
                      Page {page + 1} of {totalPages}
                    </span>
                    <button 
                      className="btn-secondary" 
                      disabled={page === totalPages - 1} 
                      onClick={() => fetchResults(page + 1)}
                    >
                      Next ▶
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </main>

      {selectedProperty && (
        <PropertyDetailsModal 
          property={selectedProperty} 
          onClose={() => setSelectedProperty(null)}
        />
      )}
    </div>
  );

  function setFormFilter(key, value) {
    setFilters(prev => ({ ...prev, [key]: value }));
  }
}
