package com.foodhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple currency conversion controller.
 * <p>
 * Provides an endpoint to convert an amount in US dollars to Indian rupees.
 * The conversion rate is hard‑coded for demonstration purposes.
 *
 * Example: GET /api/convert/10 returns {"dollars":10,"rupees":820}
 */
@RestController
@RequestMapping("/api/convert")
public class CurrencyController {

    private static final double USD_TO_INR_RATE = 82.0; // example rate

    @GetMapping("/{dollars}")
    public ResponseEntity<?> convert(@PathVariable("dollars") double dollars) {
        double rupees = dollars * USD_TO_INR_RATE;
        return ResponseEntity.ok(new ConversionResult(dollars, rupees));
    }

    // Simple DTO for JSON response
    private static class ConversionResult {
        public final double dollars;
        public final double rupees;
        public ConversionResult(double dollars, double rupees) {
            this.dollars = dollars;
            this.rupees = rupees;
        }
    }
}

