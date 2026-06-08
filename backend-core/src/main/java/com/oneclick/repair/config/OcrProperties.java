package com.oneclick.repair.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ocr.ollama")
public record OcrProperties(String baseUrl, String model) {
}
