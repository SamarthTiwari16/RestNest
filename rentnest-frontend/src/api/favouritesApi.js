import axiosClient from './axiosClient';

export function addFavourite(propertyId) {
  return axiosClient.post(`/favourites/${propertyId}`);
}

export function removeFavourite(propertyId) {
  return axiosClient.delete(`/favourites/${propertyId}`);
}

export function getMyFavourites() {
  return axiosClient.get('/favourites');
}

export function getMyFavouritePropertyIds() {
  return axiosClient.get('/favourites/ids');
}
