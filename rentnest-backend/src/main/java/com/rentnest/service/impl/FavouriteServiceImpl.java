package com.rentnest.service.impl;

import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.entity.Favourite;
import com.rentnest.entity.Property;
import com.rentnest.entity.User;
import com.rentnest.exception.ResourceNotFoundException;
import com.rentnest.repository.FavouriteRepository;
import com.rentnest.repository.PropertyRepository;
import com.rentnest.repository.UserRepository;
import com.rentnest.service.FavouriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavouriteServiceImpl implements FavouriteService {

    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public FavouriteServiceImpl(FavouriteRepository favouriteRepository, UserRepository userRepository, PropertyRepository propertyRepository) {
        this.favouriteRepository = favouriteRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public void addFavourite(Long propertyId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        if (favouriteRepository.existsByUserIdAndPropertyId(user.getId(), property.getId())) {
            return; // Return silently if already favourited
        }

        Favourite favourite = new Favourite(user, property);
        favouriteRepository.save(favourite);
    }

    @Override
    public void removeFavourite(Long propertyId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        favouriteRepository.findByUserIdAndPropertyId(user.getId(), propertyId)
                .ifPresent(favouriteRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponse> getMyFavourites(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        return favouriteRepository.findAllByUserId(user.getId())
                .stream()
                .map(f -> PropertyResponse.from(f.getProperty()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getMyFavouritePropertyIds(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        return favouriteRepository.findAllByUserId(user.getId())
                .stream()
                .map(f -> f.getProperty().getId())
                .collect(Collectors.toList());
    }
}
