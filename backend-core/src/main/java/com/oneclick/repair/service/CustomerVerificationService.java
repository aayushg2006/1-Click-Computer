package com.oneclick.repair.service;

import com.oneclick.repair.config.JwtService;
import com.oneclick.repair.dto.CustomerAccessTokenResponse;
import com.oneclick.repair.dto.CustomerAccessVerificationRequest;
import com.oneclick.repair.model.Ticket;
import com.oneclick.repair.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerVerificationService {

    private final TicketRepository ticketRepository;
    private final JwtService jwtService;

    @Value("${customer.access.token.ttl-seconds:3600}")
    private long tokenTtlSeconds;

    public CustomerAccessTokenResponse verifyInitial(CustomerAccessVerificationRequest request) {
        Ticket ticket = ticketRepository.findByTrackingCode(request.getTrackingCode())
                .orElseThrow(() -> new RuntimeException("Sorry, no repair ticket found with this code."));
        String normalizedPhone = normalize(ticket.getCustomerPhone());
        String actualLast4 = normalizedPhone.length() <= 4 ? normalizedPhone : normalizedPhone.substring(normalizedPhone.length() - 4);
        if (!actualLast4.equals(normalize(request.getMobileNumber()))) {
            throw new RuntimeException("The mobile number does not match this tracking code.");
        }
        User principal = new User(ticket.getTrackingCode(), "", List.of());
        String token = jwtService.generateToken(Map.of("scope", "customer_verified"), principal, tokenTtlSeconds * 1000);
        return CustomerAccessTokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresInSeconds(tokenTtlSeconds)
                .trackingCode(ticket.getTrackingCode())
                .issuedAt(OffsetDateTime.now())
                .build();
    }

    public void validateTokenAndLast4(String authorization, String trackingCode, String last4, String expectedPhone) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Missing verified customer access token.");
        }
        String token = authorization.substring("Bearer ".length());
        String subject = jwtService.extractUsername(token);
        if (!normalize(subject).equals(normalize(trackingCode))) {
            throw new RuntimeException("Verified access token does not match this tracking code.");
        }
        if (last4 == null || last4.isBlank()) {
            throw new RuntimeException("Missing last 4 digits verification.");
        }
        String normalizedPhone = normalize(expectedPhone);
        String actualLast4 = normalizedPhone.length() <= 4 ? normalizedPhone : normalizedPhone.substring(normalizedPhone.length() - 4);
        if (!actualLast4.equals(normalize(last4))) {
            throw new RuntimeException("The last 4 digits do not match this tracking code.");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.replaceAll("[^0-9]", "");
    }
}
