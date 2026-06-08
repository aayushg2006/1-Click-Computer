package com.oneclick.repair.service;

import com.oneclick.repair.dto.CctvEstimatorRequest;
import com.oneclick.repair.dto.CctvEstimatorResponse;
import com.oneclick.repair.model.Ticket;
import com.oneclick.repair.model.TicketUpdate;
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
public class CctvEstimatorService {

    private final TicketRepository ticketRepository;
    private final TicketUpdateRepository ticketUpdateRepository;
    private final ServiceAreaService serviceAreaService;

    @Transactional
    public CctvEstimatorResponse createEstimate(CctvEstimatorRequest request) {
        boolean deliverable = serviceAreaService.isDeliverablePinCode(request.getPinCode());
        String referenceId = "CCTV-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        BigDecimal estimatedAmount = calculateEstimate(request.getCameraCount(), request.getBackupDays(), deliverable);

        Ticket ticket = Ticket.builder()
                .trackingCode(referenceId)
                .customerName(request.getCustomerName() != null ? request.getCustomerName() : "CCTV Lead")
                .customerPhone(request.getCustomerPhone() != null ? request.getCustomerPhone() : "0000000000")
                .ticketType("CCTV_SURVEY")
                .status("LEAD_CREATED")
                .deviceDetails(buildDeviceDetails(request))
                .estimatedCost(estimatedAmount)
                .isHomeService(true)
                .visitCharge(deliverable ? BigDecimal.valueOf(499) : BigDecimal.ZERO)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        ticketUpdateRepository.save(TicketUpdate.builder()
                .ticket(savedTicket)
                .statusTitle("CCTV Lead Created")
                .statusDescription("CCTV estimate lead created for " + request.getCameraCount() + " cameras and " + request.getBackupDays() + " backup days.")
                .isVisibleToCustomer(true)
                .createdAt(OffsetDateTime.now())
                .build());

        return CctvEstimatorResponse.builder()
                .referenceId(referenceId)
                .cameraCount(request.getCameraCount())
                .backupDays(request.getBackupDays())
                .estimatedAmount(estimatedAmount)
                .withinServiceArea(deliverable)
                .leadMessage("CCTV estimate submitted successfully. Our team will contact you for site survey.")
                .ticketId(savedTicket.getId())
                .build();
    }

    private BigDecimal calculateEstimate(int cameraCount, int backupDays, boolean deliverable) {
        BigDecimal cameraCost = BigDecimal.valueOf(cameraCount).multiply(BigDecimal.valueOf(3500));
        BigDecimal storageCost = BigDecimal.valueOf(backupDays).multiply(BigDecimal.valueOf(120));
        BigDecimal installation = deliverable ? BigDecimal.valueOf(2500) : BigDecimal.valueOf(1500);
        return cameraCost.add(storageCost).add(installation);
    }

    private String buildDeviceDetails(CctvEstimatorRequest request) {
        return "CCTV estimate request: " + request.getCameraCount() + " cameras, backup days " + request.getBackupDays()
                + ", address " + (request.getAddress() != null ? request.getAddress() : "N/A")
                + ", pin " + (request.getPinCode() != null ? request.getPinCode() : "N/A");
    }
}
