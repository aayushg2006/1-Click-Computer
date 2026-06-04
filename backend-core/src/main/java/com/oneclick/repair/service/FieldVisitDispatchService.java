package com.oneclick.repair.service;

import com.oneclick.repair.dto.DoorstepRepairRequest;
import com.oneclick.repair.dto.DoorstepRepairResponse;
import com.oneclick.repair.model.FieldVisit;
import com.oneclick.repair.model.Technician;
import com.oneclick.repair.model.Ticket;
import com.oneclick.repair.repository.FieldVisitRepository;
import com.oneclick.repair.repository.TechnicianRepository;
import com.oneclick.repair.repository.TicketRepository;
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

    // Fixed charge for visiting a customer's house
    private final BigDecimal STANDARD_VISIT_CHARGE = new BigDecimal("300.00"); 

    @Transactional
    public DoorstepRepairResponse bookDoorstepService(DoorstepRepairRequest request) {
        
        // 1. Verify Pin Code (Basic check for Vasai/Virar/Nalasopara area codes which usually start with 401)
        if (request.getPinCode() == null || !request.getPinCode().startsWith("401")) {
            throw new RuntimeException("Sorry, Doorstep service is currently only available in the Vasai-Virar region. Please use Store Pickup.");
        }

        // 2. Generate a simple 6-character tracking code
        String trackCode = "TK-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        // 3. Create the Ticket exactly matching your Ticket model
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

        ticketRepository.save(newTicket);

        // 4. Send back the success response with the tracking code
        return DoorstepRepairResponse.builder()
                .trackingCode(trackCode)
                .message("Doorstep repair booked successfully! A technician will be assigned shortly.")
                .visitCharge(STANDARD_VISIT_CHARGE)
                .build();
    }

    // This method is for your private Flutter App to assign a worker to the job
    @Transactional
    public String assignTechnicianToTicket(String trackingCode, UUID technicianId, OffsetDateTime scheduledTime) {
        
        // Find the Ticket
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found!"));
        
        // Find the Technician
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician not found!"));

        // Link the Technician to the Ticket
        ticket.setAssignedTechnician(technician);
        ticket.setStatus("TECHNICIAN_DISPATCHED");
        ticketRepository.save(ticket);

        // Create the official Field Visit log exactly matching your FieldVisit model
        FieldVisit visit = FieldVisit.builder()
                .ticket(ticket)
                .technician(technician)
                .scheduledDate(scheduledTime)
                .visitStatus("SCHEDULED")
                .isChargeCollected(false)
                .build();

        fieldVisitRepository.save(visit);

        return "Technician " + technician.getFullName() + " successfully assigned to Ticket " + trackingCode;
    }
}