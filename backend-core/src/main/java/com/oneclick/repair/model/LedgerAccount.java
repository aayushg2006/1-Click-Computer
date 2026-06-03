package com.oneclick.repair.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ledger_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // We link the Khata directly to the Customer
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    // NEW ADDITION: 
    // This tells the database: "One Ledger Account has Many Transactions."
    // cascade = CascadeType.ALL means if you delete the account, it deletes the history too.
    @OneToMany(mappedBy = "ledgerAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LedgerTransaction> transactions = new ArrayList<>();

    @Column(name = "last_updated_at")
    private OffsetDateTime lastUpdatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedAt = OffsetDateTime.now();
    }
}