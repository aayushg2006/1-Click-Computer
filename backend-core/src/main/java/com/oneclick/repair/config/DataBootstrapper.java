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
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (!enabled) {
                return;
            }

            seedCategories(categoryRepository);
            seedAdminUser(appUserRepository, passwordEncoder, adminUsername, adminPassword);
            seedTechnician(appUserRepository, technicianRepository, passwordEncoder, technicianUsername, technicianPassword);
        };
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
