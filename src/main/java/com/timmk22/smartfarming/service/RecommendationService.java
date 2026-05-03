package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.response.RecommendationResponse;

public interface RecommendationService {
    RecommendationResponse generateRecommendation(Long plantingId, boolean summarized);
}
