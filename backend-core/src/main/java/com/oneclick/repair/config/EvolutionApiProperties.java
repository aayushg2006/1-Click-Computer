package com.oneclick.repair.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "evolution.api")
public record EvolutionApiProperties(String baseUrl, String instance, String key) {
}
