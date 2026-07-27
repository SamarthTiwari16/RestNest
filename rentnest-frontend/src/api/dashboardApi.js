import axios from './axiosClient.js';

export const getOwnerDashboard = () => axios.get('/api/dashboard/owner');
export const getTenantDashboard = () => axios.get('/api/dashboard/tenant');
