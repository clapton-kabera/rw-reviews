package com.kabera.rw_reviews.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;

/**
 * A service offered by a business, as submitted during registration. Prices are in RWF.
 */
public record ServiceOfferingRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull @PositiveOrZero Long minPrice,
        @NotNull @PositiveOrZero Long maxPrice) {

    /**
     * Cross-field rule: the range must not be inverted.
     * Returns true when a price is missing so only the @NotNull message is reported in that case.
     * @JsonIgnore keeps Jackson from treating this validation helper as a JSON property.
     */
    @JsonIgnore
    @AssertTrue(message = "minPrice must be less than or equal to maxPrice")
    public boolean isPriceRangeValid() {
        return minPrice == null || maxPrice == null || minPrice <= maxPrice;
    }
}
