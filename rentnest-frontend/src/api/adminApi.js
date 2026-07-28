import axiosClient from './axiosClient.js';

export const getPendingProperties = (page = 0, size = 10) => axiosClient.get(`/admin/properties/pending?page=${page}&size=${size}`);
export const approveProperty = (id) => axiosClient.post(`/admin/properties/${id}/approve`);
export const rejectProperty = (id, reason) => axiosClient.post(`/admin/properties/${id}/reject`, { reason });
export const deactivateProperty = (id) => axiosClient.post(`/admin/properties/${id}/deactivate`);
