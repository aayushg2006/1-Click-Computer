package com.oneclick.repair.controller;

import com.oneclick.repair.dto.admin.TicketAdminDTO;
import com.oneclick.repair.dto.admin.TicketUpdateDTO;
import com.oneclick.repair.dto.admin.TicketUpdateRequest;
import com.oneclick.repair.service.TicketMediaService;
import com.oneclick.repair.service.TicketAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/tickets")
@RequiredArgsConstructor
public class TicketAdminController {

    private final TicketAdminService ticketAdminService;
    private final TicketMediaService ticketMediaService;

    @GetMapping
    public ResponseEntity<List<TicketAdminDTO>> listTickets() {
        return ResponseEntity.ok(ticketAdminService.listTickets());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TicketAdminDTO>> searchTickets(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(ticketAdminService.searchTickets(query));
    }

    @GetMapping("/{trackingCode}")
    public ResponseEntity<TicketAdminDTO> getTicket(@PathVariable String trackingCode) {
        return ResponseEntity.ok(ticketAdminService.getTicket(trackingCode));
    }

    @GetMapping("/{trackingCode}/updates")
    public ResponseEntity<List<TicketUpdateDTO>> getUpdates(@PathVariable String trackingCode) {
        return ResponseEntity.ok(ticketAdminService.getUpdates(trackingCode));
    }

    @PostMapping("/{trackingCode}/updates")
    public ResponseEntity<TicketUpdateDTO> addUpdate(@PathVariable String trackingCode, @Valid @RequestBody TicketUpdateRequest request) {
        return ResponseEntity.ok(ticketAdminService.addUpdate(trackingCode, request));
    }

    @PutMapping("/{trackingCode}/assign-technician")
    public ResponseEntity<TicketAdminDTO> assignTechnician(@PathVariable String trackingCode, @RequestParam UUID technicianId) {
        return ResponseEntity.ok(ticketAdminService.assignTechnician(trackingCode, technicianId));
    }

    @PostMapping(value = "/{trackingCode}/media", consumes = {"multipart/form-data"})
    public ResponseEntity<TicketUpdateDTO> uploadMedia(
            @PathVariable String trackingCode,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption,
            @RequestParam(value = "visibleToCustomer", required = false) Boolean visibleToCustomer) {
        return ResponseEntity.ok(ticketMediaService.uploadTicketMedia(trackingCode, file, caption, visibleToCustomer));
    }
}
