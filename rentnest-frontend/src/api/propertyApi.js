import axiosClient from './axiosClient.js';

export function createProperty(data) {
  return axiosClient.post('/properties', data);
}

export function updateProperty(id, data) {
  return axiosClient.put(`/properties/${id}`, data);
}

export function getProperty(id) {
  return axiosClient.get(`/properties/${id}`);
}

export function getMyProperties() {
  return axiosClient.get('/properties/my');
}

export function submitForVerification(id) {
  return axiosClient.post(`/properties/${id}/submit`);
}

export function markAsRented(id) {
  return axiosClient.post(`/properties/${id}/rent`);
}

export function withdrawProperty(id) {
  return axiosClient.post(`/properties/${id}/withdraw`);
}

export function uploadImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  return axiosClient.post('/properties/images/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}
