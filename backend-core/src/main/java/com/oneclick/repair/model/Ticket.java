package com.oneclick.repair.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 15)
    private String customerPhone;

    @Column(name = "ticket_type", nullable = false, length = 30)
    private String ticketType; // 'LAPTOP_REPAIR', 'PC_REPAIR', 'CUSTOM_PC_BUILD', 'CCTV_INSTALL'

    @Column(nullable = false, length = 50)
    private String status = "RECEIVED"; // 'RECEIVED', 'DIAGNOSING', 'AWAITING_APPROVAL', 'REPAIRED', etc.

    @Column(name = "estimated_cost")
    private BigDecimal estimatedCost = BigDecimal.ZERO;

    @Column(name = "device_details", nullable = false, columnDefinition = "TEXT")
    private String deviceDetails;

    @Column(name = "is_home_service")
    private Boolean isHomeService = false;

    @Column(name = "visit_charge")
    private BigDecimal visitCharge = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}