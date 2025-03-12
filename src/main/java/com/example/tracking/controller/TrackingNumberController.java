package com.example.tracking.controller;

import com.example.tracking.service.TrackingNumberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TrackingNumberController {

    private final TrackingNumberService trackingNumberService;

    public TrackingNumberController(TrackingNumberService trackingNumberService) {
        this.trackingNumberService = trackingNumberService;
    }

    @GetMapping("/next-tracking-number")
    public ResponseEntity<Map<String, Object>> getTrackingNumber(
            @RequestParam String origin_country_id,
            @RequestParam String destination_country_id,
            @RequestParam String weight,
            @RequestParam String created_at,
            @RequestParam String customer_id,
            @RequestParam String customer_name,
            @RequestParam String customer_slug) {

        String trackingNumber = trackingNumberService.generateTrackingNumber(origin_country_id, destination_country_id,
                weight, created_at, customer_id, customer_name, customer_slug);

        Map<String, Object> response = Map.of(
                "tracking_number", trackingNumber,
                "created_at", Instant.now()
        );

        return ResponseEntity.ok(response);
    }
}
