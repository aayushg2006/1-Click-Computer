package com.oneclick.repair.controller;

import com.oneclick.repair.dto.PcBuildRequest;
import com.oneclick.repair.dto.PcBuildResponse;
import com.oneclick.repair.service.PcBuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController makes this a web endpoint that talks and listens in JSON data.
@RestController
// @RequestMapping sets the base web address for this feature.
@RequestMapping("/api/v1/public/pc-builder")
@RequiredArgsConstructor
public class PcBuildController {

    private final PcBuildService pcBuildService;

    // @PostMapping means this endpoint is waiting to RECEIVE data from the website.
    @PostMapping("/quote")
    public ResponseEntity<PcBuildResponse> getBuildQuote(@RequestBody PcBuildRequest request) {
        
        // Hand the order to the Chef, get the final Response box, and send it back to the customer!
        PcBuildResponse response = pcBuildService.generateBuildQuote(request);
        return ResponseEntity.ok(response);
    }
}