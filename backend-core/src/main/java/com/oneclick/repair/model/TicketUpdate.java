package com.oneclick.repair.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_updates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "status_title", nullable = false, length = 100)
    private String statusTitle;

    @Column(name = "status_description", nullable = false, columnDefinition = "TEXT")
    private String statusDescription;

    @Column(name = "media_url")
    private String mediaUrl; // URL link targeting physical files stored inside MinIO

    @Column(name = "is_visible_to_customer")
    private Boolean isVisibleToCustomer = true;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}