package com.oneclick.repair.config;

import com.oneclick.repair.model.AppUser;
import com.oneclick.repair.model.Category;
import com.oneclick.repair.model.Role;
import com.oneclick.repair.model.Technician;
import com.oneclick.repair.repository.AppUserRepository;
import com.oneclick.repair.repository.CategoryRepository;
import com.oneclick.repair.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataBootstrapper {

    @Bean
    public CommandLineRunner seedData(
            @Value("${app.bootstrap.enabled:true}") boolean enabled,
            @Value("${app.bootstrap.admin-username:admin}") String adminUsername,
            @Value("${app.bootstrap.admin-password:admin123}") String adminPassword,
            @Value("${app.bootstrap.technician-username:tech1}") String technicianUsername,
            @Value("${app.bootstrap.technician-password:tech123}") String technicianPassword,
            AppUserRepository appUserRepository,
            TechnicianRepository technicianRepository,
            CategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate) {

        return args -> {
            if (!enabled) {
                return;
            }

            seedCategories(categoryRepository);
            seedAdminUser(appUserRepository, passwordEncoder, adminUsername, adminPassword);
            seedTechnician(appUserRepository, technicianRepository, passwordEncoder, technicianUsername, technicianPassword);
            seedMockPcParts(categoryRepository, jdbcTemplate);
        };
    }

    private void seedMockPcParts(CategoryRepository categoryRepository, JdbcTemplate jdbcTemplate) {
        // Fix for previously set NOT NULL constraint blocking inserts
        try {
            jdbcTemplate.execute("ALTER TABLE pc_build_configurations ALTER COLUMN ticket_id DROP NOT NULL");
        } catch (Exception e) {
            // Ignore if already dropped or if table doesn't exist yet
        }
        Category comp = categoryRepository.findByName("Components").orElseThrow();
        String[][] parts = {
                {"11111111-1111-1111-1111-111111111111", "Intel Core i5-13400F", "18500"},
                {"22222222-2222-2222-2222-222222222222", "AMD Ryzen 5 7600", "19000"},
                {"33333333-3333-3333-3333-333333333333", "MSI PRO B760M-A WIFI", "14500"},
                {"44444444-4444-4444-4444-444444444444", "Gigabyte B650M DS3H", "15500"},
                {"55555555-5555-5555-5555-555555555555", "Corsair Vengeance 16GB DDR5", "5500"},
                {"66666666-6666-6666-6666-666666666666", "Crucial 32GB (2x16GB) DDR5", "9500"},
                {"77777777-7777-7777-7777-777777777777", "NVIDIA RTX 4060 8GB", "29000"},
                {"88888888-8888-8888-8888-888888888888", "AMD Radeon RX 7600 8GB", "26500"}
        };

        for (String[] part : parts) {
            String id = part[0];
            String name = part[1];
            String price = part[2];
            try {
                jdbcTemplate.update("INSERT INTO products (id, category_id, name, brand, buying_price, selling_price, current_stock, is_available_for_pickup) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                        java.util.UUID.fromString(id), comp.getId(), name, "Generic", new java.math.BigDecimal(price).subtract(new java.math.BigDecimal("1000")), new java.math.BigDecimal(price), 10, true);
            } catch (Exception e) {
                // Ignore if exists or H2 syntax differs (H2 might not support ON CONFLICT, use MERGE or ignore)
                try {
                    jdbcTemplate.update("MERGE INTO products (id, category_id, name, brand, buying_price, selling_price, current_stock, is_available_for_pickup) KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                            java.util.UUID.fromString(id), comp.getId(), name, "Generic", new java.math.BigDecimal(price).subtract(new java.math.BigDecimal("1000")), new java.math.BigDecimal(price), 10, true);
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }
    }

    private void seedCategories(CategoryRepository categoryRepository) {
        List.of("Accessories", "Networking", "Laptops", "CCTV", "Components").forEach(name -> {
            if (categoryRepository.findByName(name).isEmpty()) {
                categoryRepository.save(Category.builder()
                        .name(name)
                        .description(name + " category")
                        .build());
            }
        });
    }

    private void seedAdminUser(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, String username, String password) {
        if (appUserRepository.findByUsername(username).isEmpty()) {
            appUserRepository.save(AppUser.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build());
        }
    }

    private void seedTechnician(AppUserRepository appUserRepository,
                                TechnicianRepository technicianRepository,
                                PasswordEncoder passwordEncoder,
                                String username,
                                String password) {
        if (appUserRepository.findByUsername(username).isEmpty()) {
            AppUser user = appUserRepository.save(AppUser.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(Role.TECHNICIAN)
                    .isActive(true)
                    .build());

            technicianRepository.save(Technician.builder()
                    .appUser(user)
                    .fullName("Shop Technician")
                    .phoneNumber("9999999999")
                    .isActive(true)
                    .build());
        }
    }
}
