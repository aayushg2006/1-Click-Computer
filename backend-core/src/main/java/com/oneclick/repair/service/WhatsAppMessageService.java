package com.oneclick.repair.service;

import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class WhatsAppMessageService {

    public String buildDeepLink(String phoneNumber, String message) {
        try {
            String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            String normalizedPhone = phoneNumber == null ? "" : phoneNumber.replaceAll("\\D+", "");
            return "https://wa.me/" + normalizedPhone + "?text=" + encoded;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to build WhatsApp deep link", e);
        }
    }

    public String formatPcBuildNegotiation(String referenceId, String partsText, String totalText, String pinCode, boolean deliverable) {
        return "Hello 1 Click Computer! I would like to finalize my Custom PC Build.\n\n"
                + "*Reference ID:* " + referenceId + "\n"
                + "*Parts Selected:*\n"
                + partsText
                + "\n*Estimated Total:* " + totalText + "\n"
                + "My Pin Code is: " + pinCode + "\n"
                + "Delivery Zone: " + (deliverable ? "Premium doorstep delivery available" : "Self pickup only");
    }
}
