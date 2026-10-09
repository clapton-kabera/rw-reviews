package com.kabera.rw_reviews.service;

import com.kabera.rw_reviews.exception.BadRequestException;
import org.springframework.data.domain.Sort;

import java.util.Locale;

/**
 * Sort options offered when reading reviews.
 * Every option ends with a unique tie-breaker (id). Without one, rows with equal sort values
 * can come back in a different order per query, so pagination could skip or repeat reviews.
 */
public enum ReviewSort
{
    NEWEST(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))),
    HIGHEST(Sort.by(Sort.Order.desc("rating"), Sort.Order.desc("createdAt"), Sort.Order.desc("id"))),
    LOWEST(Sort.by(Sort.Order.asc("rating"), Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

    private final Sort sort;

    ReviewSort(Sort sort) {
        this.sort = sort;
    }

    public Sort toSort() {
        return sort;
    }

    /** Case-insensitive parse of the ?sort= query parameter; unknown values become a 400. */
    public static ReviewSort parse(String value) {
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException("Unknown sort '" + value + "'. Allowed values: newest, highest, lowest");
        }
    }
}
