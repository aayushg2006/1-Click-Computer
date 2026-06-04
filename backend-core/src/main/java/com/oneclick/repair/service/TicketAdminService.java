package com.oneclick.repair.service;

import com.oneclick.repair.dto.admin.TicketAdminDTO;
import com.oneclick.repair.dto.admin.TicketUpdateDTO;
import com.oneclick.repair.dto.admin.TicketUpdateRequest;
import com.oneclick.repair.model.Technician;
import com.oneclick.repair.model.Ticket;
import com.oneclick.repair.model.TicketUpdate;
import com.oneclick.repair.repository.TechnicianRepository;
import com.oneclick.repair.repository.TicketRepository;
import com.oneclick.repair.repository.TicketUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketAdminService {

    private final TicketRepository ticketRepository;
    private final TicketUpdateRepository ticketUpdateRepository;
    private final TechnicianRepository technicianRepository;

    @Transactional(readOnly = true)
    public List<TicketAdminDTO> listTickets() {
        return ticketRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketAdminDTO> searchTickets(String query) {
        String q = query == null ? "" : query.trim();
        return ticketRepository.findByTrackingCodeContainingIgnoreCaseOrCustomerPhoneContainingIgnoreCaseOrderByCreatedAtDesc(q, q)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketAdminDTO getTicket(String trackingCode) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return toDto(ticket);
    }

    @Transactional
    public TicketUpdateDTO addUpdate(String trackingCode, TicketUpdateRequest request) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        TicketUpdate update = TicketUpdate.builder()
                .ticket(ticket)
                .statusTitle(request.getStatusTitle())
                .statusDescription(request.getStatusDescription())
                .mediaUrl(request.getMediaUrl())
                .isVisibleToCustomer(request.getVisibleToCustomer() == null || request.getVisibleToCustomer())
                .build();

        return toUpdateDto(ticketUpdateRepository.save(update));
    }

    @Transactional
    public TicketAdminDTO assignTechnician(String trackingCode, UUID technicianId) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        ticket.setAssignedTechnician(technician);
        ticket.setStatus("TECHNICIAN_DISPATCHED");
        ticketRepository.save(ticket);

        TicketUpdate update = TicketUpdate.builder()
                .ticket(ticket)
                .statusTitle("Technician Assigned")
                .statusDescription("Technician " + technician.getFullName() + " assigned to this job.")
                .isVisibleToCustomer(true)
                .build();
        ticketUpdateRepository.save(update);

        return toDto(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketUpdateDTO> getUpdates(String trackingCode) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return ticketUpdateRepository.findByTicketIdOrderByCreatedAtAsc(ticket.getId()).stream()
                .map(this::toUpdateDto)
                .toList();
    }

    private TicketAdminDTO toDto(Ticket ticket) {
        return TicketAdminDTO.builder()
                .id(ticket.getId())
                .trackingCode(ticket.getTrackingCode())
                .customerName(ticket.getCustomerName())
                .customerPhone(ticket.getCustomerPhone())
                .ticketType(ticket.getTicketType())
                .status(ticket.getStatus())
                .deviceDetails(ticket.getDeviceDetails())
                .estimatedCost(ticket.getEstimatedCost())
                .assignedTechnicianName(ticket.getAssignedTechnician() != null ? ticket.getAssignedTechnician().getFullName() : null)
                .isHomeService(ticket.getIsHomeService())
                .visitCharge(ticket.getVisitCharge())
                .createdAt(ticket.getCreatedAt())
                .build();
    }

    private TicketUpdateDTO toUpdateDto(TicketUpdate update) {
        return TicketUpdateDTO.builder()
                .id(update.getId())
                .statusTitle(update.getStatusTitle())
                .statusDescription(update.getStatusDescription())
                .mediaUrl(update.getMediaUrl())
                .visibleToCustomer(update.getIsVisibleToCustomer())
                .createdAt(update.getCreatedAt())
                .build();
    }
}
