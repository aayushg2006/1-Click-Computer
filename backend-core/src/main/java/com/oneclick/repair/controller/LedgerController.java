package com.oneclick.repair.controller;

import com.oneclick.repair.dto.AddLedgerTransactionRequest;
import com.oneclick.repair.dto.LedgerSummaryDTO;
import com.oneclick.repair.service.LedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
// Protected endpoint for the admin staff
@RequestMapping("/api/v1/admin/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    // Flutter calls this to log a payment
    @PostMapping("/transaction")
    public ResponseEntity<String> addTransaction(@Valid @RequestBody AddLedgerTransactionRequest request) {
        String result = ledgerService.addTransaction(request);
        return ResponseEntity.ok(result);
    }

    // Flutter calls this to view the customer's balance screen
    @GetMapping("/{customerId}")
    public ResponseEntity<LedgerSummaryDTO> getCustomerLedger(@PathVariable UUID customerId) {
        LedgerSummaryDTO summary = ledgerService.getCustomerLedger(customerId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<LedgerSummaryDTO>> getOverdue(@RequestParam(defaultValue = "0") BigDecimal threshold) {
        return ResponseEntity.ok(ledgerService.getOverdueLedgers(threshold));
    }
}
