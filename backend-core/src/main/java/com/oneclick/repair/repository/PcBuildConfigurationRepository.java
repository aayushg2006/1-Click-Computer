package com.oneclick.repair.repository;

import com.oneclick.repair.model.PcBuildConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PcBuildConfigurationRepository extends JpaRepository<PcBuildConfiguration, UUID> {
    
    // This allows us to instantly find the PC build details using the customer's secure Ticket ID
    Optional<PcBuildConfiguration> findByTicketId(UUID ticketId);
}