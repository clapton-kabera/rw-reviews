package com.kabera.rw_reviews.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Builds safe Pageable objects from raw query-string values.
 */
public final class PagingUtil {

    /** Upper bound on page size so a client cannot request the entire table in one call. */
    public static final int MAX_PAGE_SIZE = 50;

    private PagingUtil() {
    }

    /** Clamps page to >= 0 and size into [1, MAX_PAGE_SIZE] instead of rejecting bad values. */
    public static Pageable of(int page, int size, Sort sort) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, sort);
    }
}
