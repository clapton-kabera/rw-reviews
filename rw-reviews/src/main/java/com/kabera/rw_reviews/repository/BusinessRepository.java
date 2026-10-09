package com.kabera.rw_reviews.repository;

import com.kabera.rw_reviews.model.Business;
import com.kabera.rw_reviews.model.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<Business, Long>
{
    /** Browse all businesses, optionally narrowed by a name search (pass "" for no search). */
    Page<Business> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /** Same as above but restricted to one service type. */
    Page<Business> findByServiceTypeAndNameContainingIgnoreCase(ServiceType serviceType, String name, Pageable pageable);
}
