package com.oneclick.repair.controller;

import com.oneclick.repair.dto.CctvEstimatorRequest;
import com.oneclick.repair.dto.CctvEstimatorResponse;
import com.oneclick.repair.service.CctvEstimatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/cctv-estimator")
@RequiredArgsConstructor
public class CctvEstimatorController {

    private final CctvEstimatorService cctvEstimatorService;

    @PostMapping("/estimate")
    public ResponseEntity<CctvEstimatorResponse> estimate(@Valid @RequestBody CctvEstimatorRequest request) {
        return ResponseEntity.ok(cctvEstimatorService.createEstimate(request));
    }
}
