package com.timmk22.smartfarming.controller;

import com.timmk22.smartfarming.dto.response.RecommendationResponse;
import com.timmk22.smartfarming.service.RecommendationService;
import com.timmk22.smartfarming.web.RecommendationController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RecommendationControllerTest {

    @Test
    void generateRecommendationShouldReturnRecommendationWhenServiceSucceeds() {
        RecommendationService recommendationService = mock(RecommendationService.class);
        RecommendationController controller = new RecommendationController(recommendationService);

        RecommendationResponse recommendation = mock(RecommendationResponse.class);

        when(recommendationService.generateRecommendation(1L, false)).thenReturn(recommendation);

        ResponseEntity<?> response = controller.generateRecommendation(1L, false);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recommendation);

        verify(recommendationService, times(1)).generateRecommendation(1L, false);
    }

    @Test
    void generateRecommendationShouldReturnRecommendationWhenSummarizedIsTrue() {
        RecommendationService recommendationService = mock(RecommendationService.class);
        RecommendationController controller = new RecommendationController(recommendationService);

        RecommendationResponse recommendation = mock(RecommendationResponse.class);

        when(recommendationService.generateRecommendation(5L, true)).thenReturn(recommendation);

        ResponseEntity<?> response = controller.generateRecommendation(5L, true);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recommendation);

        verify(recommendationService, times(1)).generateRecommendation(5L, true);
    }

    @Test
    void generateRecommendationShouldReturnBadRequestWhenServiceThrowsException() {
        RecommendationService recommendationService = mock(RecommendationService.class);
        RecommendationController controller = new RecommendationController(recommendationService);

        when(recommendationService.generateRecommendation(1L, false))
                .thenThrow(new RuntimeException("Failed to generate recommendation"));

        ResponseEntity<?> response = controller.generateRecommendation(1L, false);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Failed to generate recommendation");

        verify(recommendationService, times(1)).generateRecommendation(1L, false);
    }
}