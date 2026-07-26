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
}
