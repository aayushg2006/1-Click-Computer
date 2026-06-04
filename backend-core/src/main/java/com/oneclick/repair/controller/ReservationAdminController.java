package com.oneclick.repair.controller;

import com.oneclick.repair.dto.admin.StoreReservationAdminDTO;
import com.oneclick.repair.service.ReservationAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationAdminService reservationAdminService;

    @GetMapping
    public ResponseEntity<List<StoreReservationAdminDTO>> listReservations() {
        return ResponseEntity.ok(reservationAdminService.listReservations());
    }

    @GetMapping("/search")
    public ResponseEntity<List<StoreReservationAdminDTO>> searchByPhone(@RequestParam(required = false) String phone) {
        return ResponseEntity.ok(reservationAdminService.searchByPhone(phone));
    }
}
