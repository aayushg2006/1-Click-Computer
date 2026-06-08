package com.oneclick.repair.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payments.upi")
public record UpiProperties(String vpa, String payeeName) {
}
