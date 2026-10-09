package com.kabera.rw_reviews.dto;

import jakarta.validation.constraints.*;

/**
 * Payload for submitting a review. No authentication exists, so the reviewer
 * identifies themselves with a free-text display name.
 */
public record ReviewRequest(
        @NotBlank @Size(max = 80) String reviewerName,
        // Integer (not int) so a missing rating is reported as null -> @NotNull, instead of silently becoming 0
        @NotNull @Min(value = 1, message = "rating must be at least 1")
        @Max(value = 5, message = "rating must be at most 5") Integer rating,
        // The text is optional: a star rating alone is a valid review
        @Size(max = 2000) String comment) {
}
