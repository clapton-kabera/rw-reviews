package com.kabera.rw_reviews.dto;

/**
 * Aggregated rating data for one business. Built directly by the JPQL constructor
 * expression in ReviewRepository#summarize (hence the boxed types: avg() yields a Double
 * and count() yields a Long).
 */
public record RatingSummary(Long businessId, Double average, Long count) {
}
