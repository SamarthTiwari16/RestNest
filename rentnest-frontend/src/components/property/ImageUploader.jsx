import { useState } from 'react';
import * as propertyApi from '../../api/propertyApi.js';

export default function ImageUploader({ imageUrls, onChange }) {
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');

  const handleFileChange = async (e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

    setError('');
    setUploading(true);

    try {
      const uploadedUrls = [];
      for (const file of files) {
        // Basic frontend validations
        if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type)) {
          throw new Error('Only JPG, PNG, and WEBP formats are allowed');
        }
        if (file.size > 5 * 1024 * 1024) {
          throw new Error('Files must be smaller than 5MB');
        }

        const { data } = await propertyApi.uploadImage(file);
        uploadedUrls.push(data.url);
      }
      onChange([...imageUrls, ...uploadedUrls]);
    } catch (err) {
      setError(err.response?.data?.message ?? err.message ?? 'Upload failed');
    } finally {
      setUploading(false);
      // Reset input element value so same file can be uploaded again if needed
      e.target.value = '';
    }
  };

  const removeImage = (index) => {
    const updated = imageUrls.filter((_, i) => i !== index);
    onChange(updated);
  };

  const moveImage = (index, direction) => {
    const targetIndex = index + direction;
    if (targetIndex < 0 || targetIndex >= imageUrls.length) return;
    const updated = [...imageUrls];
    const temp = updated[index];
    updated[index] = updated[targetIndex];
    updated[targetIndex] = temp;
    onChange(updated);
  };

  return (
    <div className="image-uploader-section" style={{ margin: '1.5rem 0' }}>
      <label style={{ fontWeight: '600', display: 'block', marginBottom: '0.5rem' }}>
        Property Images (at least 1 image required before verification)
      </label>

      {error && <p className="form-error" role="alert" style={{ marginBottom: '0.8rem' }}>{error}</p>}

      <div className="image-uploader-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(120px, 1fr))', gap: '1rem', marginBottom: '1rem' }}>
        {imageUrls.map((url, idx) => {
          // Serve static image from backend port (8080) if URL is relative
          const fullUrl = url.startsWith('/') ? `http://localhost:8080${url}` : url;
          return (
            <div key={idx} className="image-preview-card" style={{ position: 'relative', border: '1px solid var(--fog)', borderRadius: '4px', overflow: 'hidden', backgroundColor: 'var(--clay)', aspectRatio: '1/1', display: 'flex', flexDirection: 'column' }}>
              <img 
                src={fullUrl} 
                alt={`Property preview ${idx + 1}`} 
                style={{ width: '100%', height: '75%', objectFit: 'cover' }}
              />
              <div className="image-preview-actions" style={{ display: 'flex', height: '25%', justifyContent: 'space-between', alignItems: 'center', padding: '0 0.4rem', backgroundColor: 'var(--parchment)' }}>
                <button 
                  type="button" 
                  disabled={idx === 0} 
                  onClick={() => moveImage(idx, -1)}
                  style={{ padding: '0.2rem', minWidth: 'auto', fontSize: '0.7rem', background: 'transparent', color: 'var(--charcoal)', border: 'none', cursor: idx === 0 ? 'not-allowed' : 'pointer' }}
                >
                  ◀
                </button>
                <button 
                  type="button" 
                  onClick={() => removeImage(idx)}
                  style={{ padding: '0.2rem', minWidth: 'auto', fontSize: '0.7rem', background: 'transparent', color: 'red', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}
                >
                  ✕
                </button>
                <button 
                  type="button" 
                  disabled={idx === imageUrls.length - 1} 
                  onClick={() => moveImage(idx, 1)}
                  style={{ padding: '0.2rem', minWidth: 'auto', fontSize: '0.7rem', background: 'transparent', color: 'var(--charcoal)', border: 'none', cursor: idx === imageUrls.length - 1 ? 'not-allowed' : 'pointer' }}
                >
                  ▶
                </button>
              </div>
              {idx === 0 && (
                <span style={{ position: 'absolute', top: '4px', left: '4px', backgroundColor: 'var(--charcoal)', color: 'var(--parchment)', fontSize: '0.6rem', padding: '0.1rem 0.3rem', borderRadius: '2px', fontWeight: 'bold' }}>
                  Cover
                </span>
              )}
            </div>
          );
        })}

        <label className="image-upload-dropzone" style={{ border: '2px dashed var(--fog)', borderRadius: '4px', aspectRatio: '1/1', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', cursor: 'pointer', backgroundColor: 'var(--parchment)', hover: { backgroundColor: 'var(--clay)' } }}>
          <span style={{ fontSize: '1.5rem', color: 'var(--fog)' }}>+</span>
          <span style={{ fontSize: '0.75rem', color: 'var(--charcoal)' }}>{uploading ? 'Uploading...' : 'Add Image'}</span>
          <input 
            type="file" 
            multiple 
            accept="image/jpeg,image/png,image/webp" 
            onChange={handleFileChange} 
            disabled={uploading}
            style={{ display: 'none' }} 
          />
        </label>

        <button
          type="button"
          id="btn-add-mock-image"
          onClick={() => onChange([...imageUrls, '/uploads/mock-image.jpg'])}
          style={{ border: '2px dashed var(--fog)', borderRadius: '4px', aspectRatio: '1/1', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', cursor: 'pointer', backgroundColor: 'var(--parchment)' }}
        >
          <span style={{ fontSize: '1.5rem', color: 'var(--fog)' }}>+</span>
          <span style={{ fontSize: '0.75rem', color: 'var(--charcoal)' }}>Mock Image</span>
        </button>
      </div>
    </div>
  );
}
