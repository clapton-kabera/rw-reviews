package com.kabera.rw_reviews.dto;

import com.kabera.rw_reviews.model.Business;
import com.kabera.rw_reviews.model.ServiceType;

import java.time.Instant;
import java.util.List;

/**
 * Business as returned to clients, including its rating aggregate.
 */
public record BusinessResponse(
        Long id,
        String name,
        ServiceType serviceType,
        String description,
        String district,
        String address,
        String phone,
        List<ServiceOfferingResponse> services,
        double averageRating,   // 0.0 when the business has no reviews yet
        long reviewCount,
        Instant createdAt) {

    /**
     * @param summary rating aggregate for this business; null means "no reviews yet"
     */
    public static BusinessResponse from(Business b, RatingSummary summary) {
        double average = 0.0;
        long count = 0;
        if (summary != null && summary.count() != null && summary.average() != null) {
            // Round to one decimal place for display (4.3333 -> 4.3)
            average = Math.round(summary.average() * 10.0) / 10.0;
            count = summary.count();
        }
        return new BusinessResponse(
                b.getId(), b.getName(), b.getServiceType(), b.getDescription(),
                b.getDistrict(), b.getAddress(), b.getPhone(),
                b.getServices().stream().map(ServiceOfferingResponse::from).toList(),
                average, count, b.getCreatedAt());
    }
}
