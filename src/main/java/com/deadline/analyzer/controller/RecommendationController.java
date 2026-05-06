package com.deadline.analyzer.controller;

import com.deadline.analyzer.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{personalityType}")
    public ResponseEntity<Map<String, Object>> getRecommendations(@PathVariable String personalityType) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> recommendations = recommendationService.getRecommendations(personalityType);

            response.put("success", true);
            response.put("personalityType", personalityType);
            response.put("recommendations", recommendations);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error loading recommendations");
            return ResponseEntity.status(500).body(response);
        }
    }
}