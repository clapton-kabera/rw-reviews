package com.kabera.rw_reviews.service;

import com.kabera.rw_reviews.dto.BusinessRequest;
import com.kabera.rw_reviews.dto.BusinessResponse;
import com.kabera.rw_reviews.dto.RatingSummary;
import com.kabera.rw_reviews.dto.ServiceOfferingRequest;
import com.kabera.rw_reviews.exception.ResourceNotFoundException;
import com.kabera.rw_reviews.model.Business;
import com.kabera.rw_reviews.model.ServiceOffering;
import com.kabera.rw_reviews.repository.BusinessRepository;
import com.kabera.rw_reviews.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Business registration and lookup.
 * Entity -> DTO mapping happens inside the transactions here because open-in-view is disabled
 * (lazy collections can only be read while the transaction is open).
 */
@RequiredArgsConstructor
@Service
public class BusinessService
{
    private final BusinessRepository businessRepository;
    private final ReviewRepository reviewRepository;

    /** Registers a new business together with its service offerings. */
    @Transactional
    public BusinessResponse register(BusinessRequest request) {
        Business business = new Business();
        business.setName(request.name().trim());
        business.setServiceType(request.serviceType());
        business.setDescription(request.description());
        business.setDistrict(request.district());
        business.setAddress(request.address());
        business.setPhone(request.phone());

        for (ServiceOfferingRequest s : request.services()) {
            business.addService(new ServiceOffering(s.name().trim(), s.minPrice(), s.maxPrice()));
        }

        // Offerings are saved too, via cascade = ALL on Business.services
        Business saved = businessRepository.save(business);
        // A brand-new business cannot have reviews, so pass no summary
        return BusinessResponse.from(saved, null);
    }

    /** Fetches one business with its current rating aggregate. */
    @Transactional(readOnly = true)
    public BusinessResponse get(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business " + id + " not found"));
        RatingSummary summary = summarize(java.util.List.of(id)).get(id);
        return BusinessResponse.from(business, summary);
    }

    /** Rating aggregates keyed by business id. */
    private Map<Long, RatingSummary> summarize(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return reviewRepository.summarize(ids).stream()
                .collect(Collectors.toMap(RatingSummary::businessId, Function.identity()));
    }
}
