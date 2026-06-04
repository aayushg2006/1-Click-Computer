package com.oneclick.repair.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AddLedgerTransactionRequest {
    // The ID of the customer making the payment or taking the credit
    @NotNull
    private UUID customerId; 
    
    @NotNull
    private BigDecimal amount;
    
    // "DEBIT" (They bought something on credit, balance goes UP) 
    // "CREDIT" (They paid you money, balance goes DOWN)
    @NotBlank
    private String transactionType; 
    
    private String remarks; // e.g., "Paid ₹5000 advance via GPay"
}
