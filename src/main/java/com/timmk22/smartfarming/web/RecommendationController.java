package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.response.RecommendationResponse;
import com.timmk22.smartfarming.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // If needed, maybe put this method inside "PlantingInformationController"
    @PostMapping("/generate/{plantingId}")
    public ResponseEntity<?> generateRecommendation(@PathVariable Long plantingId,
                                                    @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "false") boolean summarized) {
        try {
            RecommendationResponse recommendation = recommendationService.generateRecommendation(plantingId, summarized);
            return ResponseEntity.ok(recommendation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}