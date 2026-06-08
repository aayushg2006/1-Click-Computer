package com.oneclick.repair.controller;

import com.oneclick.repair.dto.CustomerAccessTokenResponse;
import com.oneclick.repair.dto.CustomerAccessVerificationRequest;
import com.oneclick.repair.dto.CustomerVerifiedInvoiceDTO;
import com.oneclick.repair.dto.CustomerVerifiedTicketDTO;
import com.oneclick.repair.service.CustomerAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/customer-access")
@RequiredArgsConstructor
public class CustomerAccessController {

    private final CustomerAccessService customerAccessService;

    @PostMapping("/verify")
    public ResponseEntity<CustomerAccessTokenResponse> verify(@Valid @RequestBody CustomerAccessVerificationRequest request) {
        return ResponseEntity.ok(customerAccessService.verify(request));
    }

    @GetMapping("/tickets/{trackingCode}")
    public ResponseEntity<CustomerVerifiedTicketDTO> getTicket(@PathVariable String trackingCode,
                                                               @RequestHeader(name = "Authorization", required = false) String authorization) {
        customerAccessService.validateAccessToken(authorization, trackingCode);
        return ResponseEntity.ok(customerAccessService.getVerifiedTicket(trackingCode));
    }

    @GetMapping("/tickets/{trackingCode}/invoices")
    public ResponseEntity<List<CustomerVerifiedInvoiceDTO>> listInvoices(@PathVariable String trackingCode,
                                                                        @RequestHeader(name = "Authorization", required = false) String authorization) {
        customerAccessService.validateAccessToken(authorization, trackingCode);
        return ResponseEntity.ok(customerAccessService.listVerifiedInvoices(trackingCode));
    }

    @GetMapping("/tickets/{trackingCode}/invoices/{invoiceNumber}")
    public ResponseEntity<CustomerVerifiedInvoiceDTO> getInvoice(@PathVariable String trackingCode,
                                                                 @PathVariable String invoiceNumber,
                                                                 @RequestHeader(name = "Authorization", required = false) String authorization) {
        customerAccessService.validateAccessToken(authorization, trackingCode);
        return ResponseEntity.ok(customerAccessService.getVerifiedInvoice(trackingCode, invoiceNumber));
    }
}
