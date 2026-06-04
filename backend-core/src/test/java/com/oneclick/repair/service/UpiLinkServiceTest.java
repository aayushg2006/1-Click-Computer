package com.oneclick.repair.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UpiLinkServiceTest {

    @Test
    void returnsPaymentLinkWhenVpaConfigured() {
        UpiLinkService service = new UpiLinkService();
        ReflectionTestUtils.setField(service, "vpa", "merchant@upi");
        ReflectionTestUtils.setField(service, "payeeName", "1 Click Computer");

        String link = service.buildPaymentLink(BigDecimal.TEN, "test");
        assertTrue(link.contains("merchant@upi"));
    }
}
