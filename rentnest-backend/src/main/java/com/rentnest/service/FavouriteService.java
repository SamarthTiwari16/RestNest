package com.rentnest.service;

import com.rentnest.dto.response.PropertyResponse;
import java.util.List;

public interface FavouriteService {
    void addFavourite(Long propertyId, String userEmail);
    void removeFavourite(Long propertyId, String userEmail);
    List<PropertyResponse> getMyFavourites(String userEmail);
    List<Long> getMyFavouritePropertyIds(String userEmail);
}
