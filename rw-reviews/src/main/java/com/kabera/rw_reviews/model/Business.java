package com.kabera.rw_reviews.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "businesses")
public class Business
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 30)
    private ServiceType serviceType;

    @Column(length = 1000)
    private String description;

    @Column(length = 80)
    private String district;

    @Column(length = 200)
    private String address;

    @Column(length = 30)
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /**
     * The services this business offers, each with a price range.
     * - cascade ALL + orphanRemoval: offerings live and die with their business.
     * - @BatchSize: when listing many businesses, load their offerings in batches
     * instead of one query per business (avoids the N+1 problem).
     */
    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    @BatchSize(size = 50)
    private List<ServiceOffering> services = new ArrayList<>();
}
