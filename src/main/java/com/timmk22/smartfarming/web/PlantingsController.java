package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.request.CreatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.request.UpdatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.response.EntryAiTipsResponse;
import com.timmk22.smartfarming.dto.response.PlantingInformationResponse;
import com.timmk22.smartfarming.dto.response.RecommendationResponse;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.repository.PlantingInformationRepository;
import com.timmk22.smartfarming.service.EntryAiTipsService;
import com.timmk22.smartfarming.service.PdfReportService;
import com.timmk22.smartfarming.service.PlantingInformationService;
import com.timmk22.smartfarming.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plantings")
public class PlantingsController {

    private final PlantingInformationService plantingInformationService;
    private final RecommendationService recommendationService;
    private final EntryAiTipsService entryAiTipsService;
    private final PdfReportService pdfReportService;
    private final PlantingInformationRepository plantingInformationRepository;

    public PlantingsController(PlantingInformationService plantingInformationService,
                               RecommendationService recommendationService,
                               EntryAiTipsService entryAiTipsService,
                               PdfReportService pdfReportService,
                               PlantingInformationRepository plantingInformationRepository) {
        this.plantingInformationService = plantingInformationService;
        this.recommendationService = recommendationService;
        this.entryAiTipsService = entryAiTipsService;
        this.pdfReportService = pdfReportService;
        this.plantingInformationRepository = plantingInformationRepository;
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

    @GetMapping("/{id}/ai-tips")
    public ResponseEntity<?> getEntryAiTips(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            EntryAiTipsResponse response = entryAiTipsService.generateAiTips(id, user.getUserId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", HttpStatus.NOT_FOUND.value(),
                    "error", HttpStatus.NOT_FOUND.getReasonPhrase(),
                    "message", e.getMessage(),
                    "path", "/api/plantings/" + id + "/ai-tips"
            ));
        }
    }

    @GetMapping(value = "/{id}/report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePlantingReport(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            PlantingInformation planting = plantingInformationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Planting not found"));

            if (!planting.getUser().getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            byte[] pdfBytes = pdfReportService.generateReportFromDatabase(
                    List.of(planting),
                    planting.getCrop().getName() + " Report",
                    LocalDate.now().toString(),
                    user.getUsername() != null ? user.getUsername() : "User"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename("planting-" + (planting.getLocationName() != null ? planting.getLocationName() : "entry")
                                    + "-" + LocalDate.now() + ".pdf")
                            .build()
            );

            return ResponseEntity.ok().headers(headers).body(pdfBytes);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
