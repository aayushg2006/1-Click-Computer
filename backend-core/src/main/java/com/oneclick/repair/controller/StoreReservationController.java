package com.oneclick.repair.controller;

import com.oneclick.repair.dto.StoreReservationRequest;
import com.oneclick.repair.dto.StoreReservationResponse;
import com.oneclick.repair.service.StoreReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController tells Spring Boot: "This class is a web endpoint that talks in JSON."
@RestController
// @RequestMapping sets the base URL for this specific feature.
// "public" means anyone on the internet can access it without logging in.
@RequestMapping("/api/v1/public/reservations")
@RequiredArgsConstructor
public class StoreReservationController {

    // We bring in the Service (The Chef) so the Controller can hand off the work.
    private final StoreReservationService reservationService;

    // @PostMapping means this endpoint expects the website to SEND (POST) data to it.
    @PostMapping("/create")
    public ResponseEntity<StoreReservationResponse> createReservation(
            // @RequestBody tells Spring to take the incoming JSON from Next.js 
            // and automatically convert it into our Java DTO box.
            @Valid @RequestBody StoreReservationRequest request) {
        
        // The Waiter hands the order to the Chef, and gets the final receipt back.
        StoreReservationResponse response = reservationService.createReservation(request);
        
        // We send the receipt back to the website with a "200 OK" success status.
        return ResponseEntity.ok(response);
    }
}
