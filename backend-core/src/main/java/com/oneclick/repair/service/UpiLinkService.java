package com.oneclick.repair.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;

@Service
public class UpiLinkService {

    @Value("${payments.upi.vpa:}")
    private String vpa;

    @Value("${payments.upi.payee-name:1 Click Computer}")
    private String payeeName;

    public String buildPaymentLink(BigDecimal amount, String note) {
        if (vpa == null || vpa.isBlank()) {
            return null;
        }
        try {
            String encodedName = URLEncoder.encode(payeeName, StandardCharsets.UTF_8.toString());
            String encodedNote = URLEncoder.encode(note == null ? "Payment" : note, StandardCharsets.UTF_8.toString());
            return "upi://pay?pa=" + vpa
                    + "&pn=" + encodedName
                    + "&am=" + amount
                    + "&cu=INR"
                    + "&tn=" + encodedNote;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to build UPI payment link", e);
        }
    }
}
