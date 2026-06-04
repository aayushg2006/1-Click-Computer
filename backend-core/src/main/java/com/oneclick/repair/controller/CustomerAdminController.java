package com.oneclick.repair.controller;

import com.oneclick.repair.dto.admin.CustomerAdminDTO;
import com.oneclick.repair.service.CustomerAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/customers")
@RequiredArgsConstructor
public class CustomerAdminController {

    private final CustomerAdminService customerAdminService;

    @GetMapping
    public ResponseEntity<List<CustomerAdminDTO>> listCustomers() {
        return ResponseEntity.ok(customerAdminService.listCustomers());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerAdminDTO>> searchCustomers(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(customerAdminService.searchCustomers(query));
    }

    @GetMapping("/by-phone")
    public ResponseEntity<CustomerAdminDTO> findByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(customerAdminService.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Customer not found")));
    }
}
