package com.oneclick.repair.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "customer.access")
public record CustomerAccessProperties(long tokenTtlSeconds) {
}
