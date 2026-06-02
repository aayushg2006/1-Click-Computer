package com.oneclick.repair.repository;

import com.oneclick.repair.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    // Find all active repairs or builds for a specific customer phone number
    List<Ticket> findByCustomerPhone(String customerPhone);
    
    // Find tickets by their current status (e.g., 'DIAGNOSING', 'REPAIRED')
    List<Ticket> findByStatus(String status);
}