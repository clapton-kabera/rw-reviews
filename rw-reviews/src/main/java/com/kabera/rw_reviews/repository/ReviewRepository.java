package com.kabera.rw_reviews.repository;

import com.kabera.rw_reviews.dto.RatingSummary;
import com.kabera.rw_reviews.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>
{
    /** One page of reviews for a business; ordering comes from the Pageable. */
    Page<Review> findByBusinessId(Long businessId, Pageable pageable);

    /**
     * Average rating and review count for several businesses in a SINGLE query.
     * Businesses without reviews simply produce no row.
     * Computing this live (instead of storing a denormalised average on Business)
     * means the numbers can never drift out of sync, even with concurrent writes.
     */
    @Query("""
            select new com.kabera.rw_reviews.dto.RatingSummary(r.business.id, avg(r.rating), count(r))
            from Review r
            where r.business.id in :ids
            group by r.business.id
            """)
    List<RatingSummary> summarize(@Param("ids") Collection<Long> ids);
}
