package com.rentnest.repository;

import com.rentnest.entity.Enquiry;
import com.rentnest.entity.EnquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {
    List<Enquiry> findAllByTenantId(Long tenantId);
    List<Enquiry> findAllByOwnerId(Long ownerId);
    boolean existsByPropertyIdAndTenantIdAndStatusIn(Long propertyId, Long tenantId, List<EnquiryStatus> statuses);
    long countByOwnerIdAndProperty_Owner_Id(Long ownerId, Long propertyOwnerId);
    long countByOwnerIdIn(java.util.Collection<Long> propertyOwnerIds);
    long countByTenantIdAndStatus(Long tenantId, EnquiryStatus status);
    long countByOwnerId(Long ownerId);
}

