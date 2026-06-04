package com.oneclick.repair.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ServiceAreaService {

    private static final Set<String> SUPPORTED_PREFIXES = Set.of("401", "400", "402");

    public boolean isDeliverablePinCode(String pinCode) {
        if (pinCode == null) {
            return false;
        }
        String normalized = pinCode.trim();
        return SUPPORTED_PREFIXES.stream().anyMatch(normalized::startsWith);
    }
}
