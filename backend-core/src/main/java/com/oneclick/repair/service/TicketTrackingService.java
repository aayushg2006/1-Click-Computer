package com.oneclick.repair.service;

import com.oneclick.repair.dto.admin.TicketUpdateDTO;
import com.oneclick.repair.dto.TicketTrackingDTO;
import com.oneclick.repair.model.Ticket;
import com.oneclick.repair.repository.TicketRepository;
import com.oneclick.repair.repository.TicketUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketTrackingService {

    private final TicketRepository ticketRepository;
    private final TicketUpdateRepository ticketUpdateRepository;

    @Transactional(readOnly = true)
    public TicketTrackingDTO getTicketStatus(String trackingCode) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Sorry, no repair ticket found with this code."));

        String maskedName = maskCustomerName(ticket.getCustomerName());

        return TicketTrackingDTO.builder()
                .trackingCode(ticket.getTrackingCode())
                .customerNameMasked(maskedName)
                .ticketType(ticket.getTicketType())
                .status(ticket.getStatus())
                .deviceDetails(ticket.getDeviceDetails())
                .conditionPhotoUrls(ticket.getConditionPhotoUrls())
                .approvedEstimatedCost(ticket.getEstimatedCost() != null
                        ? "INR " + ticket.getEstimatedCost()
                        : "Pending Diagnosis")
                .build();
    }

    @Transactional(readOnly = true)
    public List<TicketUpdateDTO> getPublicUpdates(String trackingCode) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Sorry, no repair ticket found with this code."));
        return ticketUpdateRepository.findByTicketIdOrderByCreatedAtAsc(ticket.getId()).stream()
                .filter(update -> update.getIsVisibleToCustomer() == null || update.getIsVisibleToCustomer())
                .map(update -> TicketUpdateDTO.builder()
                        .id(update.getId())
                        .statusTitle(update.getStatusTitle())
                        .statusDescription(update.getStatusDescription())
                        .mediaUrl(update.getMediaUrl())
                        .visibleToCustomer(update.getIsVisibleToCustomer())
                        .createdAt(update.getCreatedAt())
                        .build())
                .toList();
    }

    private String maskCustomerName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Customer";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length > 1) {
            return parts[0] + " " + parts[1].charAt(0) + ".";
        }
        return parts[0];
    }
}
