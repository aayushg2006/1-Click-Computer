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
    
    // The total amount they owe you right now
    private BigDecimal currentBalance; 
    
    // The history of their payments to display on the app screen
    private List<TransactionRecord> recentTransactions;

    @Data
    @Builder
    public static class TransactionRecord {
        private String transactionType;
        private BigDecimal amount;
        private String remarks;
        private OffsetDateTime date;
    }
}
