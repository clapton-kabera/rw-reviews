package com.kabera.rw_reviews.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single service a business offers (e.g. "Swedish massage") with its price range.
 * Prices are whole Rwandan francs (RWF), which has no minor unit, so a Long is enough.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "service_offerings")
public class ServiceOffering
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "min_price", nullable = false)
    private Long minPrice;

    @Column(name = "max_price", nullable = false)
    private Long maxPrice;

    public ServiceOffering(String name, Long minPrice, Long maxPrice)
    {
        this.name = name;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}
