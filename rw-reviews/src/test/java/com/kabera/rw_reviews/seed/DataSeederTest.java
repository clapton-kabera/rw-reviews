package com.kabera.rw_reviews.seed;

import com.kabera.rw_reviews.repository.BusinessRepository;
import com.kabera.rw_reviews.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that startup seeding populates the database.
 * The test profile disables seeding by default; here we switch it back on for this class only.
 */
@SpringBootTest(properties = "app.seed.enabled=true")
@ActiveProfiles("test")
class DataSeederTest {

    @Autowired
    BusinessRepository businessRepository;
    @Autowired
    ReviewRepository reviewRepository;

    @Test
    void seedsBusinessesAndReviewsOnStartup() {
        assertThat(businessRepository.count()).isGreaterThanOrEqualTo(5);
        // Enough reviews that pagination has multiple pages to walk through
        assertThat(reviewRepository.count()).isGreaterThan(100);
    }

    // Services are a lazy collection, so reading them needs an open transaction (open-in-view is off)
    @Test
    @Transactional
    void everySeededBusinessHasServices() {
        businessRepository.findAll().forEach(b ->
                assertThat(b.getServices()).as("services of %s", b.getName()).isNotEmpty());
    }
}
