package com.kabera.rw_reviews.controller;

import com.kabera.rw_reviews.model.Business;
import com.kabera.rw_reviews.model.ServiceOffering;
import com.kabera.rw_reviews.model.ServiceType;

/** Small factory so tests can create valid businesses in one line. */
final class TestData {

    private TestData() {
    }

    static Business business(String name, ServiceType type) {
        Business b = new Business();
        b.setName(name);
        b.setServiceType(type);
        b.setDistrict("Gasabo");
        b.addService(new ServiceOffering("Basic service", 1000L, 5000L));
        return b;
    }
}