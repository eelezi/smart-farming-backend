package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.request.CreatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.request.UpdatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.response.PlantingInformationResponse;
import com.timmk22.smartfarming.service.PlantingInformationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/entries")
public class PlantingInformationController {

    private final PlantingInformationService plantingInformationService;

    public PlantingInformationController(PlantingInformationService plantingInformationService) {
        this.plantingInformationService = plantingInformationService;
    }

    /**
     * Fetch all entries for the logged-in user.
     *
     * @param userId the user ID (obtained from request header)
     * @return list of all entries for the user
     */
    @GetMapping
    public ResponseEntity<List<PlantingInformationResponse>> getAllEntries(
            @RequestHeader("User-Id") Long userId) {
        try {
            List<PlantingInformationResponse> entries = plantingInformationService.getAllEntries(userId);
            return ResponseEntity.ok(entries);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Fetch details of a specific entry.
     *
     * @param id     the entry ID
     * @param userId the user ID (obtained from request header)
     * @return details of the specified entry
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlantingInformationResponse> getEntryById(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            PlantingInformationResponse entry = plantingInformationService.getEntryById(id, userId);
            return ResponseEntity.ok(entry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Create a new entry.
     *
     * @param request the create request DTO
     * @param userId  the user ID (obtained from request header)
     * @return the created entry
     */
    @PostMapping
    public ResponseEntity<?> createEntry(
            @Valid @RequestBody CreatePlantingInformationRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            PlantingInformationResponse entry = plantingInformationService.createEntry(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(entry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Update an existing entry.
     *
     * @param id      the entry ID
     * @param request the update request DTO
     * @param userId  the user ID (obtained from request header)
     * @return the updated entry
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntry(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlantingInformationRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            PlantingInformationResponse entry = plantingInformationService.updateEntry(id, request, userId);
            return ResponseEntity.ok(entry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Remove an entry.
     *
     * @param id     the entry ID
     * @param userId the user ID (obtained from request header)
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            plantingInformationService.deleteEntry(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
