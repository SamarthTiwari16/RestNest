import axios from './axiosClient.js';

export const getOwnerDashboard = () => axios.get('/dashboard/owner');
export const getTenantDashboard = () => axios.get('/dashboard/tenant');
