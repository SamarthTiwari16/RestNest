package com.rentnest.repository;

import com.rentnest.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    Optional<Favourite> findByUserIdAndPropertyId(Long userId, Long propertyId);
    List<Favourite> findAllByUserId(Long userId);
    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
}
