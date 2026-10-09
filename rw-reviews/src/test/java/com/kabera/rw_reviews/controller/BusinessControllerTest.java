package com.kabera.rw_reviews.controller;

import com.kabera.rw_reviews.model.ServiceType;
import com.kabera.rw_reviews.repository.BusinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * End-to-end tests of the business endpoints (HTTP -> controller -> service -> H2).
 * "test" profile: seeding is disabled and each context gets its own database.
 * @Transactional rolls back each test's data so tests cannot affect one another.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BusinessControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    BusinessRepository businessRepository;

    @Test
    void registerBusiness_returns201WithLocationAndBody() throws Exception {
        String body = """
                {
                  "name": "Test Cafe",
                  "serviceType": "OTHER",
                  "district": "Gasabo",
                  "services": [
                    {"name": "Coffee", "minPrice": 1500, "maxPrice": 3000},
                    {"name": "Cake", "minPrice": 2000, "maxPrice": 4000}
                  ]
                }""";

        mvc.perform(post("/api/businesses").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test Cafe"))
                .andExpect(jsonPath("$.services.length()").value(2))
                .andExpect(jsonPath("$.reviewCount").value(0))
                .andExpect(jsonPath("$.averageRating").value(0.0));
    }

    @Test
    void registerBusiness_missingFields_returns400WithFieldErrors() throws Exception {
        // No name, no service type, no services
        mvc.perform(post("/api/businesses").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.serviceType").exists())
                .andExpect(jsonPath("$.errors.services").exists());
    }

    @Test
    void registerBusiness_invertedPriceRange_returns400() throws Exception {
        String body = """
                {
                  "name": "Bad Prices Ltd",
                  "serviceType": "OTHER",
                  "services": [{"name": "Thing", "minPrice": 5000, "maxPrice": 1000}]
                }""";

        mvc.perform(post("/api/businesses").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBusiness_unknownId_returns404() throws Exception {
        mvc.perform(get("/api/businesses/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listBusinesses_filtersByServiceTypeAndName() throws Exception {
        businessRepository.save(TestData.business("Alpha Coffee", ServiceType.CAFE));
        businessRepository.save(TestData.business("Beta Coffee", ServiceType.CAFE));
        businessRepository.save(TestData.business("Alpha Garage", ServiceType.GARAGE));

        // Type filter only
        mvc.perform(get("/api/businesses").param("serviceType", "CAFE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));

        // Type + case-insensitive name search
        mvc.perform(get("/api/businesses").param("serviceType", "CAFE").param("q", "alpha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Alpha Coffee"));
    }
}