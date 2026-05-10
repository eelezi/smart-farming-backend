package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.response.EntryAiTipsResponse;

/**
 * Service for generating AI-based cultivation tips and recommendations for planting entries.
 */
public interface EntryAiTipsService {

    /**
     * Generate AI-based tips and recommendations for a specific planting entry.
     *
     * @param entryId the ID of the planting entry
     * @param userId  the user ID (for ownership verification)
     * @return AI-generated tips and recommendations, or a fallback response if the AI service
     *         is unavailable or fails
     * @throws IllegalArgumentException if entry not found or user doesn't own it
     */
    EntryAiTipsResponse generateAiTips(Long entryId, Long userId);
}

