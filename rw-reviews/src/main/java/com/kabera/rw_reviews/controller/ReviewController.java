package com.kabera.rw_reviews.controller;

import com.kabera.rw_reviews.dto.PageResponse;
import com.kabera.rw_reviews.dto.ReviewRequest;
import com.kabera.rw_reviews.dto.ReviewResponse;
import com.kabera.rw_reviews.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Reviews are a sub-resource of a business, so every URL is nested under it.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/businesses/{businessId}/reviews")

public class ReviewController {

    private final ReviewService reviewService;

    /** POST /api/businesses/{businessId}/reviews - submit a review (no auth needed). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(@PathVariable Long businessId,
                                 @Valid @RequestBody ReviewRequest request) {
        return reviewService.create(businessId, request);
    }

    /**
     * GET /api/businesses/{businessId}/reviews?page=0&size=10&sort=newest
     * Paginated; sort is one of newest (default), highest, lowest.
     */
    @GetMapping
    public PageResponse<ReviewResponse> list(@PathVariable Long businessId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "newest") String sort) {
        return reviewService.list(businessId, page, size, sort);
    }
}
