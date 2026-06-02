package com.oneclick.repair.repository;

import com.oneclick.repair.model.FieldVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FieldVisitRepository extends JpaRepository<FieldVisit, UUID> {
    
    // Find all scheduled visits for a specific ticket
    List<FieldVisit> findByTicketId(UUID ticketId);
    
    // Find all jobs assigned to a specific worker
    List<FieldVisit> findByTechnicianId(UUID technicianId);
}