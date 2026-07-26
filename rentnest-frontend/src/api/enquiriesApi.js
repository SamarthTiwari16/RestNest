import axiosClient from './axiosClient';

export function sendEnquiry(propertyId, payload) {
  return axiosClient.post(`/enquiries/property/${propertyId}`, payload);
}

export function acceptEnquiry(enquiryId) {
  return axiosClient.post(`/enquiries/${enquiryId}/accept`);
}

export function declineEnquiry(enquiryId) {
  return axiosClient.post(`/enquiries/${enquiryId}/decline`);
}

export function getSentEnquiries() {
  return axiosClient.get('/enquiries/sent');
}

export function getReceivedEnquiries() {
  return axiosClient.get('/enquiries/received');
}
