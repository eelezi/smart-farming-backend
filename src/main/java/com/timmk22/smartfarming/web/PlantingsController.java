package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.request.CreatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.request.UpdatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.response.PlantingInformationResponse;
import com.timmk22.smartfarming.dto.response.RecommendationResponse;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.service.PlantingInformationService;
import com.timmk22.smartfarming.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plantings")
public class PlantingsController {

    private final PlantingInformationService plantingInformationService;
    private final RecommendationService recommendationService;

    public PlantingsController(PlantingInformationService plantingInformationService,
                                          RecommendationService recommendationService) {
        this.plantingInformationService = plantingInformationService;
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<PlantingInformationResponse>> getAllEntries(
            @AuthenticationPrincipal User user) {
        try {
            List<PlantingInformationResponse> entries = plantingInformationService.getAllEntries(user.getUserId());
            return ResponseEntity.ok(entries);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantingInformationResponse> getEntryById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            PlantingInformationResponse entry = plantingInformationService.getEntryById(id, user.getUserId());
            return ResponseEntity.ok(entry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createEntry(
            @Valid @RequestBody CreatePlantingInformationRequest request,
            @AuthenticationPrincipal User user) {
        try {
            PlantingInformationResponse entry = plantingInformationService.createEntry(request, user.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(entry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntry(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlantingInformationRequest request,
            @AuthenticationPrincipal User user) {
        try {
            PlantingInformationResponse entry = plantingInformationService.updateEntry(id, request, user.getUserId());
            return ResponseEntity.ok(entry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            plantingInformationService.deleteEntry(id, user.getUserId());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/recommendation")
    public ResponseEntity<?> generateRecommendation(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean summarized) {
        try {
            RecommendationResponse recommendation = recommendationService.generateRecommendation(id, summarized);
            return ResponseEntity.ok(recommendation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
