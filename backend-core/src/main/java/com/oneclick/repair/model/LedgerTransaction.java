package com.oneclick.repair.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ledger_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ledger_id", nullable = false)
    private LedgerAccount ledgerAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType; // 'CREDIT' or 'DEBIT'

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "transaction_date", updatable = false)
    private OffsetDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        this.transactionDate = OffsetDateTime.now();
    }
}