package com.oneclick.repair.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "store_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Links this reservation to the specific product they are holding.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 15)
    private String customerPhone;

    // The time they promised to come to the Nallasopara store (e.g., "14:00 - 16:00")
    @Column(name = "pickup_timeslot", nullable = false)
    private String pickupTimeslot;

    // Status can be: 'HELD', 'COLLECTED', or 'EXPIRED' (if they never showed up)
    @Column(nullable = false, length = 30)
    private String status = "HELD";

    @Column(name = "reserved_at", updatable = false)
    private OffsetDateTime reservedAt;

    @PrePersist
    protected void onCreate() {
        this.reservedAt = OffsetDateTime.now();
    }
}