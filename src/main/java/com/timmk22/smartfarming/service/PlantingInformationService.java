package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.request.CreatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.request.UpdatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.response.PlantingInformationResponse;

import java.util.List;

public interface PlantingInformationService {

    /**
     * Fetch all planting information entries for a specific user.
     *
     * @param userId the user ID
     * @return list of planting information responses
     */
    List<PlantingInformationResponse> getAllEntries(Long userId);

    /**
     * Fetch a specific planting information entry by ID with ownership check.
     *
     * @param entryId the entry ID
     * @param userId  the user ID (for ownership check)
     * @return planting information response
     * @throws IllegalArgumentException if entry not found or user doesn't own it
     */
    PlantingInformationResponse getEntryById(Long entryId, Long userId);

    /**
     * Create a new planting information entry.
     *
     * @param request the create request DTO
     * @param userId  the user ID (owner)
     * @return created planting information response
     * @throws IllegalArgumentException if referenced crop or soil type doesn't exist
     */
    PlantingInformationResponse createEntry(CreatePlantingInformationRequest request, Long userId);

    /**
     * Update an existing planting information entry with ownership check.
     *
     * @param entryId the entry ID
     * @param request the update request DTO
     * @param userId  the user ID (for ownership check)
     * @return updated planting information response
     * @throws IllegalArgumentException if entry not found, user doesn't own it, or references don't exist
     */
    PlantingInformationResponse updateEntry(Long entryId, UpdatePlantingInformationRequest request, Long userId);

    /**
     * Delete a planting information entry with ownership check.
     *
     * @param entryId the entry ID
     * @param userId  the user ID (for ownership check)
     * @throws IllegalArgumentException if entry not found or user doesn't own it
     */
    void deleteEntry(Long entryId, Long userId);
} 