package com.oneclick.repair.repository;

import com.oneclick.repair.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    
    // Spring Security will use this to find the user during login
    Optional<AppUser> findByUsername(String username);
}