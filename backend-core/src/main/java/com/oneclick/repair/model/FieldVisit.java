package com.oneclick.repair.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "field_visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Link back to the customer's repair/CCTV ticket
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    // Link to the assigned worker
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @Column(name = "scheduled_date", nullable = false)
    private OffsetDateTime scheduledDate;

    @Column(name = "visit_status", length = 50)
    private String visitStatus = "SCHEDULED";

    @Column(name = "visit_notes", columnDefinition = "TEXT")
    private String visitNotes;

    @Column(name = "is_charge_collected")
    private Boolean isChargeCollected = false;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}