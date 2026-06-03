package com.oneclick.repair.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pc_build_configurations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PcBuildConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Links this specific PC configuration to a customer's tracking ticket
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "total_parts_cost", nullable = false)
    private BigDecimal totalPartsCost = BigDecimal.ZERO;

    @Column(name = "assembly_charge", nullable = false)
    private BigDecimal assemblyCharge = BigDecimal.ZERO;

    @Column(name = "is_compatibility_verified")
    private Boolean isCompatibilityVerified = false;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}