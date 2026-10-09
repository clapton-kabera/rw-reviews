package com.kabera.rw_reviews.controller;

import com.kabera.rw_reviews.dto.BusinessRequest;
import com.kabera.rw_reviews.dto.BusinessResponse;
import com.kabera.rw_reviews.dto.PageResponse;
import com.kabera.rw_reviews.model.ServiceType;
import com.kabera.rw_reviews.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Endpoints for registering and browsing businesses.
 */
@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    /** POST /api/businesses - register a business. Responds 201 with a Location header. */
    @PostMapping
    public ResponseEntity<BusinessResponse> register(@Valid @RequestBody BusinessRequest request) {
        BusinessResponse created = businessService.register(request);
        // Location: .../api/businesses/{newId}
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /** GET /api/businesses/{id} - one business with its rating summary. */
    @GetMapping("/{id}")
    public BusinessResponse get(@PathVariable Long id) {
        return businessService.get(id);
    }

    /**
     * GET /api/businesses?serviceType=CAFE&q=coffee&page=0&size=10
     * All parameters are optional. Page numbers are zero-based; size is capped at 50.
     */
    @GetMapping
    public PageResponse<BusinessResponse> list(
            @RequestParam(required = false) ServiceType serviceType,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return businessService.list(serviceType, q, page, size);
    }
}
