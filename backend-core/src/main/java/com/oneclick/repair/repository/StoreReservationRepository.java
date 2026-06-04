package com.oneclick.repair.repository;

import com.oneclick.repair.model.StoreReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreReservationRepository extends JpaRepository<StoreReservation, UUID> {
    // We can add custom queries here later if we need to find reservations by a specific customer phone number
    List<StoreReservation> findByCustomerPhoneContainingIgnoreCaseOrderByReservedAtDesc(String customerPhone);
}
