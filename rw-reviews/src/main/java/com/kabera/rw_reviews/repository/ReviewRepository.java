package com.kabera.rw_reviews.repository;

import com.kabera.rw_reviews.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>
{
}
