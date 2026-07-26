package com.rentnest.service;

import com.rentnest.dto.request.EnquiryRequest;
import com.rentnest.dto.response.EnquiryResponse;
import java.util.List;

public interface EnquiryService {
    EnquiryResponse sendEnquiry(Long propertyId, EnquiryRequest request, String tenantEmail);
    EnquiryResponse acceptEnquiry(Long enquiryId, String ownerEmail);
    EnquiryResponse declineEnquiry(Long enquiryId, String ownerEmail);
    List<EnquiryResponse> getSentEnquiries(String tenantEmail);
    List<EnquiryResponse> getReceivedEnquiries(String ownerEmail);
}
