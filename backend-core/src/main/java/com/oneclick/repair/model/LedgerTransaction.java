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

    // NEW ADDITION: THE MISSING LINK
    // This links this specific payment directly to the customer's main Khata account.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ledger_account_id", nullable = false)
    private LedgerAccount ledgerAccount;

    // Type should be either 'DEBIT' (they bought something, balance goes up) 
    // or 'CREDIT' (they paid you money, balance goes down)
    @Column(name = "transaction_type", nullable = false, length = 15)
    private String transactionType;

    @Column(nullable = false)
    private BigDecimal amount;

    // Optional: If this transaction is because of a specific invoice, we link it here!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    // A simple note like "Paid via UPI" or "Advance for Custom PC"
    @Column(length = 255)
    private String remarks;

    @Column(name = "transaction_date", updatable = false)
    private OffsetDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        this.transactionDate = OffsetDateTime.now();
    }
}