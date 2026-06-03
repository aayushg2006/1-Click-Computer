package com.oneclick.repair.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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

    // 1. THE TRACKING CODE
    // This is the short, simple code (like 'TK-A8F2') the customer types on the website.
    // 'unique = true' means no two tickets can ever have the same tracking code.
    @Column(name = "tracking_code", unique = true, nullable = false, length = 10)
    private String trackingCode; 

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 15)
    private String customerPhone;

    @Column(name = "ticket_type", nullable = false, length = 30)
    private String ticketType;

    @Column(nullable = false, length = 50)
    private String status = "RECEIVED";

    @Column(name = "estimated_cost")
    private BigDecimal estimatedCost = BigDecimal.ZERO;

    @Column(name = "device_details", nullable = false, columnDefinition = "TEXT")
    private String deviceDetails;

    // 2. HARDWARE CONDITION PHOTOS
    // @ElementCollection tells the database to create a small, hidden table just to store 
    // a list of text strings (in this case, the URLs to the photos saved in your MinIO storage).
    @ElementCollection
    @CollectionTable(name = "ticket_photos", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "photo_url")
    private List<String> conditionPhotoUrls = new ArrayList<>();

    @Column(name = "is_home_service")
    private Boolean isHomeService = false;

    @Column(name = "visit_charge")
    private BigDecimal visitCharge = BigDecimal.ZERO;

    // 3. TECHNICIAN DISPATCH
    // @ManyToOne means Many tickets can be assigned to One technician.
    // It links this ticket directly to the Technician table.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private Technician assignedTechnician;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}