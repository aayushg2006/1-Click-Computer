package com.oneclick.repair.repository;

import com.oneclick.repair.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    // Crucial for the Khata system: Looking up a customer by their phone number
    Optional<Customer> findByPhoneNumber(String phoneNumber);
}