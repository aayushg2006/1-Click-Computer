package com.oneclick.repair.controller;

import com.oneclick.repair.dto.DoorstepRepairRequest;
import com.oneclick.repair.dto.DoorstepRepairResponse;
import com.oneclick.repair.service.FieldVisitDispatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FieldVisitController {

    private final FieldVisitDispatchService dispatchService;

    // 1. PUBLIC ENDPOINT: For the Next.js website (Customers booking a repair)
    @PostMapping("/public/repairs/doorstep")
    public ResponseEntity<DoorstepRepairResponse> bookDoorstepRepair(@Valid @RequestBody DoorstepRepairRequest request) {
        DoorstepRepairResponse response = dispatchService.bookDoorstepService(request);
        return ResponseEntity.ok(response);
    }

    // 2. PRIVATE ENDPOINT: For your Flutter App (Admin assigning a technician)
    // Later, we will protect this with Spring Security so only you can call it.
    @PostMapping("/admin/visits/assign")
    public ResponseEntity<String> assignTechnician(
            @RequestParam String trackingCode, 
            @RequestParam UUID technicianId) {
        
        // For simplicity, scheduling them for right now
        OffsetDateTime scheduledTime = OffsetDateTime.now();
        
        String result = dispatchService.assignTechnicianToTicket(trackingCode, technicianId, scheduledTime);
        return ResponseEntity.ok(result);
    }
}
