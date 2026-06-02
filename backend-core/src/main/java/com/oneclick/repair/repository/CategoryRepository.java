package com.oneclick.repair.repository;

import com.oneclick.repair.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // Spring automatically writes the SQL to find a category by its exact name
    Optional<Category> findByName(String name);
}