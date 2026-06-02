package com.oneclick.repair.repository;

import com.oneclick.repair.model.LedgerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, UUID> {
    Optional<LedgerAccount> findByCustomerId(UUID customerId);

    // Find all customers who owe you money (Udhaar) - useful for WhatsApp reminders!
    List<LedgerAccount> findByCurrentBalanceLessThan(BigDecimal threshold);
}