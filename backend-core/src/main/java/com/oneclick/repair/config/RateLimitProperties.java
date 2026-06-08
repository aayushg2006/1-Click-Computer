package com.oneclick.repair.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.rate-limit.public")
public record RateLimitProperties(long limit, long windowSeconds) {
}
