package com.kabera.rw_reviews.service;

import com.kabera.rw_reviews.dto.PageResponse;
import com.kabera.rw_reviews.dto.ReviewRequest;
import com.kabera.rw_reviews.dto.ReviewResponse;
import com.kabera.rw_reviews.exception.ResourceNotFoundException;
import com.kabera.rw_reviews.model.Business;
import com.kabera.rw_reviews.model.Review;
import com.kabera.rw_reviews.repository.BusinessRepository;
import com.kabera.rw_reviews.repository.ReviewRepository;
import com.kabera.rw_reviews.util.PagingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Writing and reading reviews.
 */
@Service
public class ReviewService
{
    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;
    private final BusinessRepository businessRepository;

    public ReviewService(ReviewRepository reviewRepository, BusinessRepository businessRepository) {
        this.reviewRepository = reviewRepository;
        this.businessRepository = businessRepository;
    }

    /** Adds a review to a business. Anyone can do this (no authentication). */
    @Transactional
    public ReviewResponse create(Long businessId, ReviewRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business " + businessId + " not found"));

        Review review = new Review(
                business,
                request.reviewerName().trim(),
                request.rating(),
                request.comment(),
                Instant.now());

        ReviewResponse response = ReviewResponse.from(reviewRepository.save(review));
        log.info("Review {} created for business {} (rating={})", response.id(), businessId, response.rating());
        return response;
    }

    /**
     * One page of a business's reviews.
     *
     * @param sort newest (default), highest or lowest
     */
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> list(Long businessId, int page, int size, String sort) {
        // Distinguish "business doesn't exist" (404) from "business has no reviews yet" (empty page)
        if (!businessRepository.existsById(businessId)) {
            throw new ResourceNotFoundException("Business " + businessId + " not found");
        }

        Pageable pageable = PagingUtil.of(page, size, ReviewSort.parse(sort).toSort());
        return PageResponse.from(reviewRepository.findByBusinessId(businessId, pageable)
                .map(ReviewResponse::from));
    }
}
