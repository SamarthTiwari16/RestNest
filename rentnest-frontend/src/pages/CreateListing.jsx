import { useState } from 'react';
import * as propertyApi from '../api/propertyApi.js';
import ImageUploader from '../components/property/ImageUploader.jsx';

export default function CreateListing({ propertyToEdit, onCancel, onSuccess }) {
  const isEdit = !!propertyToEdit;
  const [form, setForm] = useState({
    title: propertyToEdit?.title ?? '',
    city: propertyToEdit?.city ?? '',
    locality: propertyToEdit?.locality ?? '',
    rent: propertyToEdit?.rent ?? '',
    bhk: propertyToEdit?.bhk ?? '',
    propertyType: propertyToEdit?.propertyType ?? 'APARTMENT',
    furnished: propertyToEdit?.furnished ?? false,
    petFriendly: propertyToEdit?.petFriendly ?? false,
    parking: propertyToEdit?.parking ?? false,
    availableFrom: propertyToEdit?.availableFrom ?? '',
    imageUrls: propertyToEdit?.images?.map(img => img.imageUrl) ?? [],
  });

  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setIsSubmitting(true);

    const payload = {
      ...form,
      rent: parseFloat(form.rent),
      bhk: parseInt(form.bhk, 10),
    };

    try {
      if (isEdit) {
        await propertyApi.updateProperty(propertyToEdit.id, payload);
      } else {
        await propertyApi.createProperty(payload);
      }
      onSuccess();
    } catch (err) {
      setError(err.response?.data?.message ?? 'Failed to save property listing. Please check inputs.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div>
      <header className="navbar">
        <h1>RentNest</h1>
        <div className="nav-links">
          <button className="btn-secondary" style={{ color: 'var(--parchment)', borderColor: 'var(--parchment)' }} onClick={onCancel}>Back to listings</button>
        </div>
      </header>

      <main className="container">
        <div className="form-card">
          <p className="eyebrow">{isEdit ? 'Update Draft' : 'New Listing'}</p>
          <h1 style={{ marginBottom: '2rem' }}>{isEdit ? 'Edit Property Details' : 'List your property'}</h1>

          <form onSubmit={handleSubmit} className="auth-form">
            <label>
              Listing Title
              <input 
                required 
                maxLength="150" 
                placeholder="e.g. Spacious 2BHK flat with balcony"
                value={form.title} 
                onChange={(e) => setForm({ ...form, title: e.target.value })} 
              />
            </label>

            <div className="form-row">
              <label>
                City
                <input 
                  required 
                  maxLength="100" 
                  placeholder="e.g. Bangalore"
                  value={form.city} 
                  onChange={(e) => setForm({ ...form, city: e.target.value })} 
                />
              </label>
              <label>
                Locality
                <input 
                  required 
                  maxLength="100" 
                  placeholder="e.g. Indiranagar"
                  value={form.locality} 
                  onChange={(e) => setForm({ ...form, locality: e.target.value })} 
                />
              </label>
            </div>

            <div className="form-row">
              <label>
                Monthly Rent (₹)
                <input 
                  required 
                  type="number" 
                  min="1" 
                  step="any"
                  placeholder="25000"
                  value={form.rent} 
                  onChange={(e) => setForm({ ...form, rent: e.target.value })} 
                />
              </label>
              <label>
                BHK Configuration
                <input 
                  required 
                  type="number" 
                  min="1" 
                  placeholder="2"
                  value={form.bhk} 
                  onChange={(e) => setForm({ ...form, bhk: e.target.value })} 
                />
              </label>
            </div>

            <div className="form-row">
              <label>
                Property Type
                <select 
                  value={form.propertyType} 
                  onChange={(e) => setForm({ ...form, propertyType: e.target.value })}
                >
                  <option value="APARTMENT">Apartment</option>
                  <option value="INDEPENDENT_HOUSE">Independent House</option>
                  <option value="VILLA">Villa</option>
                  <option value="STUDIO">Studio</option>
                </select>
              </label>
              <label>
                Available From
                <input 
                  required 
                  type="date" 
                  value={form.availableFrom} 
                  onChange={(e) => setForm({ ...form, availableFrom: e.target.value })} 
                />
              </label>
            </div>

            <div style={{ display: 'grid', gap: '0.8rem', margin: '0.5rem 0' }}>
              <label className="checkbox-label">
                <input 
                  type="checkbox" 
                  checked={form.furnished} 
                  onChange={(e) => setForm({ ...form, furnished: e.target.checked })} 
                />
                Fully Furnished
              </label>
              <label className="checkbox-label">
                <input 
                  type="checkbox" 
                  checked={form.petFriendly} 
                  onChange={(e) => setForm({ ...form, petFriendly: e.target.checked })} 
                />
                Pet Friendly
              </label>
              <label className="checkbox-label">
                <input 
                  type="checkbox" 
                  checked={form.parking} 
                  onChange={(e) => setForm({ ...form, parking: e.target.checked })} 
                />
                Parking Slot Available
              </label>
            </div>

            <ImageUploader 
              imageUrls={form.imageUrls} 
              onChange={(urls) => setForm({ ...form, imageUrls: urls })} 
            />

            {error && <p className="form-error" role="alert">{error}</p>}

            <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
              <button 
                type="submit" 
                disabled={isSubmitting}
                style={{ flex: 1 }}
              >
                {isSubmitting ? 'Saving...' : 'Save as Draft'}
              </button>
              <button 
                type="button" 
                className="btn-secondary" 
                onClick={onCancel}
                style={{ flex: 1 }}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
}
