package com.oneclick.repair.repository;

import com.oneclick.repair.model.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, UUID> {
    // Spring Boot automatically provides all the save(), findById(), and delete() methods!
}