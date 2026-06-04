package com.oneclick.repair.service;

import com.oneclick.repair.dto.DoorstepRepairRequest;
import com.oneclick.repair.dto.DoorstepRepairResponse;
import com.oneclick.repair.model.FieldVisit;
import com.oneclick.repair.model.Technician;
import com.oneclick.repair.model.Ticket;
import com.oneclick.repair.model.TicketUpdate;
import com.oneclick.repair.repository.FieldVisitRepository;
import com.oneclick.repair.repository.TechnicianRepository;
import com.oneclick.repair.repository.TicketRepository;
import com.oneclick.repair.repository.TicketUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldVisitDispatchService {

    private final TicketRepository ticketRepository;
    private final TechnicianRepository technicianRepository;
    private final FieldVisitRepository fieldVisitRepository;
    private final TicketUpdateRepository ticketUpdateRepository;
    private final ServiceAreaService serviceAreaService;

    private final BigDecimal STANDARD_VISIT_CHARGE = new BigDecimal("300.00");

    @Transactional
    public DoorstepRepairResponse bookDoorstepService(DoorstepRepairRequest request) {
        if (!serviceAreaService.isDeliverablePinCode(request.getPinCode())) {
            throw new RuntimeException("Sorry, Doorstep service is currently only available in the Vasai-Virar region. Please use Store Pickup.");
        }

        String trackCode = "TK-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Ticket newTicket = Ticket.builder()
                .trackingCode(trackCode)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .ticketType("DOORSTEP_REPAIR")
                .status("RECEIVED")
                .deviceDetails(request.getDeviceDetails() + "\nAddress: " + request.getAddress())
                .isHomeService(true)
                .visitCharge(STANDARD_VISIT_CHARGE)
                .build();

        Ticket savedTicket = ticketRepository.save(newTicket);

        ticketUpdateRepository.save(TicketUpdate.builder()
                .ticket(savedTicket)
                .statusTitle("Ticket Created")
                .statusDescription("Doorstep repair request received and waiting for technician assignment.")
                .isVisibleToCustomer(true)
                .build());

        return DoorstepRepairResponse.builder()
                .trackingCode(trackCode)
                .message("Doorstep repair booked successfully! A technician will be assigned shortly.")
                .visitCharge(STANDARD_VISIT_CHARGE)
                .build();
    }

    @Transactional
    public String assignTechnicianToTicket(String trackingCode, UUID technicianId, OffsetDateTime scheduledTime) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found!"));

        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician not found!"));

        ticket.setAssignedTechnician(technician);
        ticket.setStatus("TECHNICIAN_DISPATCHED");
        ticketRepository.save(ticket);

        FieldVisit visit = FieldVisit.builder()
                .ticket(ticket)
                .technician(technician)
                .scheduledDate(scheduledTime)
                .visitStatus("SCHEDULED")
                .isChargeCollected(false)
                .build();

        fieldVisitRepository.save(visit);

        ticketUpdateRepository.save(TicketUpdate.builder()
                .ticket(ticket)
                .statusTitle("Technician Dispatched")
                .statusDescription("Technician " + technician.getFullName() + " has been assigned to this job.")
                .isVisibleToCustomer(true)
                .build());

        return "Technician " + technician.getFullName() + " successfully assigned to Ticket " + trackingCode;
    }
}
