package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LedgerSummaryDTO {
    private UUID ledgerAccountId;
    private String customerName;
    private String customerPhone;
    
    // The total amount they owe right now
    private BigDecimal currentBalance;
    
    // A mini-list of their payment history to show on the app screen
    private List<TransactionRecord> recentTransactions;

    // A small "nested" class just for this list
    @Data
    @Builder
    public static class TransactionRecord {
        private String transactionType; // "CREDIT" or "DEBIT"
        private BigDecimal amount;
        private String remarks; // e.g., "Paid advance via UPI"
        private OffsetDateTime date;
    }
}