package com.oneclick.repair.repository;

import com.oneclick.repair.model.PcBuildItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PcBuildItemRepository extends JpaRepository<PcBuildItem, UUID> {
    
    // Fetches every single component mapped to a specific PC build
    List<PcBuildItem> findByBuildConfigurationId(UUID buildConfigurationId);
}