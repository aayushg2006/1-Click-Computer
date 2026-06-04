package com.oneclick.repair.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WhatsAppMessageServiceTest {

    private final WhatsAppMessageService service = new WhatsAppMessageService();

    @Test
    void buildsDeepLink() {
        String link = service.buildDeepLink("+91 99999 99999", "Hello there");
        assertTrue(link.startsWith("https://wa.me/919999999999?text="));
    }
}
