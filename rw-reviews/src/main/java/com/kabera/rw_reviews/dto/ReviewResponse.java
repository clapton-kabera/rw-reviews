package com.kabera.rw_reviews.dto;

import com.kabera.rw_reviews.model.Review;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        Long businessId,
        String reviewerName,
        int rating,
        String comment,
        Instant createdAt) {

    public static ReviewResponse from(Review r) {
        // getId() on the lazy business proxy does not trigger an extra query
        return new ReviewResponse(r.getId(), r.getBusiness().getId(), r.getReviewerName(),
                r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
