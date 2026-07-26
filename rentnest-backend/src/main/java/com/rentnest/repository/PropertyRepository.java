package com.rentnest.repository;

import com.rentnest.entity.Property;
import com.rentnest.entity.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    List<Property> findAllByOwnerId(Long ownerId);
    List<Property> findAllByStatus(PropertyStatus status);
    long countByOwnerId(Long ownerId);
    long countByOwnerIdAndStatus(Long ownerId, PropertyStatus status);
}
