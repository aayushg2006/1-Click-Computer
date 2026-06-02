package com.oneclick.repair.repository;

import com.oneclick.repair.model.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, UUID> {
    List<Technician> findByIsActiveTrue();
}