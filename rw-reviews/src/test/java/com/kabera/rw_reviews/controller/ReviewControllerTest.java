package com.kabera.rw_reviews.controller;

import com.kabera.rw_reviews.model.Business;
import com.kabera.rw_reviews.model.Review;
import com.kabera.rw_reviews.model.ServiceType;
import com.kabera.rw_reviews.repository.BusinessRepository;
import com.kabera.rw_reviews.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the core feature: writing reviews and reading them back paginated.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReviewControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    BusinessRepository businessRepository;
    @Autowired
    ReviewRepository reviewRepository;

    private Business business;

    @BeforeEach
    void setUp() {
        business = businessRepository.save(TestData.business("Review Target", ServiceType.RESTAURANT));
    }

    private String reviewsUrl() {
        return "/api/businesses/" + business.getId() + "/reviews";
    }

    /** Stores n reviews with ratings cycling 1,2,3,4,5,1,2,... so the average is predictable. */
    private void saveReviews(int n) {
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            reviews.add(new Review(business, "Reviewer " + i, (i % 5) + 1, "Comment " + i, Instant.now()));
        }
        reviewRepository.saveAll(reviews);
    }

    // ---------- writing ----------

    @Test
    void submitReview_withoutAuth_returns201() throws Exception {
        String body = """
                {"reviewerName": "Aline U.", "rating": 5, "comment": "Wonderful!"}""";

        mvc.perform(post(reviewsUrl()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.businessId").value(business.getId()))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.reviewerName").value("Aline U."));
    }

    @Test
    void submitReviews_updatesBusinessAverageAndCount() throws Exception {
        for (int rating : new int[]{5, 4}) {
            String body = "{\"reviewerName\": \"Tester\", \"rating\": " + rating + "}";
            mvc.perform(post(reviewsUrl()).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isCreated());
        }

        mvc.perform(get("/api/businesses/" + business.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewCount").value(2))
                .andExpect(jsonPath("$.averageRating").value(4.5));
    }

    @Test
    void submitReview_ratingOutOfRange_returns400() throws Exception {
        for (int rating : new int[]{0, 6}) {
            String body = "{\"reviewerName\": \"Tester\", \"rating\": " + rating + "}";
            mvc.perform(post(reviewsUrl()).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.rating").exists());
        }
    }

    @Test
    void submitReview_missingReviewerName_returns400() throws Exception {
        mvc.perform(post(reviewsUrl()).contentType(MediaType.APPLICATION_JSON).content("{\"rating\": 3}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.reviewerName").exists());
    }

    @Test
    void submitReview_unknownBusiness_returns404() throws Exception {
        mvc.perform(post("/api/businesses/999999/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reviewerName\": \"Tester\", \"rating\": 4}"))
                .andExpect(status().isNotFound());
    }

    // ---------- reading / pagination ----------

    @Test
    void listReviews_isPaginated() throws Exception {
        saveReviews(25);

        // First page: full
        mvc.perform(get(reviewsUrl()).param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.last").value(false));

        // Last page: the remaining 5
        mvc.perform(get(reviewsUrl()).param("page", "2").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.last").value(true));

        // Past the end: empty page, not an error
        mvc.perform(get(reviewsUrl()).param("page", "10").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void listReviews_defaultsToFirstPageOfTen() throws Exception {
        saveReviews(15);

        mvc.perform(get(reviewsUrl()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void listReviews_pageSizeIsCappedAt50() throws Exception {
        saveReviews(60);

        mvc.perform(get(reviewsUrl()).param("size", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(50))
                .andExpect(jsonPath("$.size").value(50));
    }

    @Test
    void listReviews_newestFirstByDefault() throws Exception {
        // Explicit timestamps so the expected order is unambiguous
        Instant now = Instant.now();
        reviewRepository.save(new Review(business, "Old", 3, "old", now.minusSeconds(3600)));
        reviewRepository.save(new Review(business, "New", 3, "new", now));

        mvc.perform(get(reviewsUrl()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reviewerName").value("New"))
                .andExpect(jsonPath("$.content[1].reviewerName").value("Old"));
    }

    @Test
    void listReviews_canSortByRating() throws Exception {
        saveReviews(10);

        mvc.perform(get(reviewsUrl()).param("sort", "highest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].rating").value(5));

        mvc.perform(get(reviewsUrl()).param("sort", "lowest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].rating").value(1));
    }

    @Test
    void listReviews_unknownSort_returns400() throws Exception {
        mvc.perform(get(reviewsUrl()).param("sort", "funniest"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReviews_unknownBusiness_returns404() throws Exception {
        mvc.perform(get("/api/businesses/999999/reviews"))
                .andExpect(status().isNotFound());
    }

    @Test
    void businessSummary_reflectsAverageRating() throws Exception {
        saveReviews(25); // ratings 1..5 repeated five times -> average exactly 3.0

        mvc.perform(get("/api/businesses/" + business.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewCount").value(25))
                .andExpect(jsonPath("$.averageRating").value(3.0));
    }
}