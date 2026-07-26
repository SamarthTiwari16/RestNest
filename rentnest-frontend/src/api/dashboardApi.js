import axios from './axios.js';

export const getOwnerDashboard = () => axios.get('/api/dashboard/owner');
export const getTenantDashboard = () => axios.get('/api/dashboard/tenant');
