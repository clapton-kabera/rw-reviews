package com.kabera.rw_reviews.dto;

import com.kabera.rw_reviews.model.ServiceOffering;

public record ServiceOfferingResponse(Long id, String name, Long minPrice, Long maxPrice) {

    public static ServiceOfferingResponse from(ServiceOffering s) {
        return new ServiceOfferingResponse(s.getId(), s.getName(), s.getMinPrice(), s.getMaxPrice());
    }
}
