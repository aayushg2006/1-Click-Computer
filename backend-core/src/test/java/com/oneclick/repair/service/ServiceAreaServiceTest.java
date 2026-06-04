package com.oneclick.repair.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ServiceAreaServiceTest {

    private final ServiceAreaService serviceAreaService = new ServiceAreaService();

    @Test
    void acceptsSupportedPincodePrefixes() {
        assertTrue(serviceAreaService.isDeliverablePinCode("401209"));
        assertTrue(serviceAreaService.isDeliverablePinCode("402001"));
    }

    @Test
    void rejectsUnsupportedPincodePrefixes() {
        assertFalse(serviceAreaService.isDeliverablePinCode("560001"));
    }
}
