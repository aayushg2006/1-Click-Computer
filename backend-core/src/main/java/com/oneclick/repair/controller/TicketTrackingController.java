package com.oneclick.repair.controller;

import com.oneclick.repair.dto.TicketTrackingDTO;
import com.oneclick.repair.dto.admin.TicketUpdateDTO;
import com.oneclick.repair.service.TicketTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/tickets")
@RequiredArgsConstructor
public class TicketTrackingController {

    private final TicketTrackingService ticketTrackingService;

    @GetMapping("/{trackingCode}")
    public ResponseEntity<TicketTrackingDTO> getTicketStatus(@PathVariable String trackingCode) {
        return ResponseEntity.ok(ticketTrackingService.getTicketStatus(trackingCode));
    }

    @GetMapping("/{trackingCode}/updates")
    public ResponseEntity<List<TicketUpdateDTO>> getTicketUpdates(@PathVariable String trackingCode) {
        return ResponseEntity.ok(ticketTrackingService.getPublicUpdates(trackingCode));
    }
}
