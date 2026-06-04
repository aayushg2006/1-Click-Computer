package com.oneclick.repair.repository;

import com.oneclick.repair.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    
    List<Ticket> findByCustomerPhone(String customerPhone);
    
    List<Ticket> findByStatus(String status);

    List<Ticket> findByTrackingCodeContainingIgnoreCaseOrCustomerPhoneContainingIgnoreCaseOrderByCreatedAtDesc(String trackingCode, String customerPhone);

    // NEW ADDITION: 
    // This allows the backend to instantly find a repair ticket just by looking 
    // up the short code the customer typed into the website.
    // We use "Optional" because the customer might type a wrong code, meaning the ticket might not exist.
    Optional<Ticket> findByTrackingCode(String trackingCode);
}
