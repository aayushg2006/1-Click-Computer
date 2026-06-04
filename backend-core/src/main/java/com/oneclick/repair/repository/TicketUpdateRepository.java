package com.oneclick.repair.repository;

import com.oneclick.repair.model.TicketUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketUpdateRepository extends JpaRepository<TicketUpdate, UUID> {
    List<TicketUpdate> findByTicketIdOrderByCreatedAtAsc(UUID ticketId);
}
