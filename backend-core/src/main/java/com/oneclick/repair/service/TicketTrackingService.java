package com.oneclick.repair.service;

import com.oneclick.repair.dto.TicketTrackingDTO;
import com.oneclick.repair.model.Ticket;
import com.oneclick.repair.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketTrackingService {

    // Bring in the worker that talks to the Ticket database table
    private final TicketRepository ticketRepository;

    // @Transactional(readOnly = true) makes the database search faster 
    // because we are only reading data, not saving or changing anything.
    @Transactional(readOnly = true)
    public TicketTrackingDTO getTicketStatus(String trackingCode) {
        
        // 1. Ask the database to find the ticket.
        // If the customer types a wrong code, we throw an error saying it wasn't found.
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Sorry, no repair ticket found with this code."));

        // 2. Hide sensitive data (Masking the customer's name for privacy)
        // For example, "Aayush Gupta" becomes "Aayush G."
        String maskedName = maskCustomerName(ticket.getCustomerName());

        // 3. Convert the raw database Model into our safe DTO box
        return TicketTrackingDTO.builder()
                .trackingCode(ticket.getTrackingCode())
                .customerNameMasked(maskedName)
                .ticketType(ticket.getTicketType())
                .status(ticket.getStatus())
                .deviceDetails(ticket.getDeviceDetails())
                .conditionPhotoUrls(ticket.getConditionPhotoUrls())
                // Convert the money value to a simple string
                .approvedEstimatedCost(ticket.getEstimatedCost() != null ? 
                        "₹" + ticket.getEstimatedCost().toString() : "Pending Diagnosis")
                .build();
    }

    // A small helper tool to safely mask names
    private String maskCustomerName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Customer";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length > 1) {
            // First name + first letter of Last name + dot
            return parts[0] + " " + parts[1].charAt(0) + ".";
        }
        return parts[0];
    }
}