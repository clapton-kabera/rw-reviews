package com.kabera.rw_reviews.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Stable, minimal JSON shape for paginated results.
 * We avoid returning Spring's PageImpl directly because its JSON structure is not
 * guaranteed to stay the same between Spring Data versions.
 */
public record PageResponse<T>(
        List<T> content,
        int page,            // zero-based index of this page
        int size,            // requested page size
        long totalElements,  // total items across all pages
        int totalPages,
        boolean last) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
