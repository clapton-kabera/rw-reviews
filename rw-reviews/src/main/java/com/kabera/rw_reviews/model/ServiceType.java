package com.kabera.rw_reviews.model;

/**
 * Category of a business (the "service type" shown when browsing/filtering).
 * Stored as a string in the DB so the data stays readable and reordering this enum is safe.
 */
public enum ServiceType {
    RESTAURANT,
    CAFE,
    BAR,
    HOTEL,
    GUESTHOUSE,
    SALON,
    SPA,
    GARAGE,
    TOUR_OPERATOR,
    SHOPPING,
    HEALTH_CLINIC,
    OTHER
}