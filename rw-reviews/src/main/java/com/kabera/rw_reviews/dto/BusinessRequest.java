package com.kabera.rw_reviews.dto;

import com.kabera.rw_reviews.model.ServiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Payload for registering a business. Using a DTO (rather than the entity) means clients
 * cannot set fields like id or createdAt, and the API shape is decoupled from the DB schema.
 */
public record BusinessRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull ServiceType serviceType,
        @Size(max = 1000) String description,
        @Size(max = 80) String district,
        @Size(max = 200) String address,
        @Size(max = 30) String phone,
        // @Valid cascades validation into every list element
        @NotEmpty @Valid List<ServiceOfferingRequest> services) {
}