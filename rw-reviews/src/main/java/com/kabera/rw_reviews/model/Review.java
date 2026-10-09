package com.kabera.rw_reviews.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * A review (1-5 star rating plus optional text) left for a business.
 * There is no authentication, so the reviewer is just a free-text display name.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "reviews",
        indexes = @Index(name = "idx_reviews_business_created", columnList = "business_id, created_at"))
public class Review
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "reviewer_name", nullable = false, length = 80)
    private String reviewerName;

    /**
     * 1 (worst) to 5 (best); range enforced by request validation.
     */
    @Column(nullable = false)
    private int rating;

    // Column named explicitly to stay clear of SQL keywords
    @Column(name = "comment_text", length = 2000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Review(Business business, String reviewerName, int rating, String comment, Instant createdAt)
    {
        this.business = business;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }
}
