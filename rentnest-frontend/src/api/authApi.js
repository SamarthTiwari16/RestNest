import axiosClient from './axiosClient.js';
export const register = (payload) => axiosClient.post('/auth/register', payload);
export const login = (payload) => axiosClient.post('/auth/login', payload);
export const getCurrentUser = () => axiosClient.get('/users/me');
