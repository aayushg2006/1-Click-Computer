package com.oneclick.repair.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap")
public record AppBootstrapProperties(boolean enabled, String adminUsername, String adminPassword, String technicianUsername, String technicianPassword) {
}
