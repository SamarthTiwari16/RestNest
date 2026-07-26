package com.rentnest.repository;

import com.rentnest.entity.RecentlyViewed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentlyViewedRepository extends JpaRepository<RecentlyViewed, Long> {
    Optional<RecentlyViewed> findByUserIdAndPropertyId(Long userId, Long propertyId);
    List<RecentlyViewed> findTop5ByUserIdOrderByViewedAtDesc(Long userId);
    List<RecentlyViewed> findAllByUserIdOrderByViewedAtDesc(Long userId);
}
