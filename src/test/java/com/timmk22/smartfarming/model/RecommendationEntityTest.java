package com.timmk22.smartfarming.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationEntityTest {

    @Test
    @DisplayName("Should create recommendation and set fields")
    void shouldCreateRecommendationAndSetFields() {
        PlantingInformation plantingInformation = new PlantingInformation();
        plantingInformation.setPlantingId(1L);

        Recommendation recommendation = new Recommendation();
        recommendation.setRecommendationId(10L);
        recommendation.setRecommendationText("Irrigate tomorrow morning.");
        recommendation.setCreatedAt(LocalDateTime.of(2026, 5, 6, 9, 30));
        recommendation.setPlantingInformation(plantingInformation);

        assertThat(recommendation.getRecommendationId()).isEqualTo(10L);
        assertThat(recommendation.getRecommendationText()).isEqualTo("Irrigate tomorrow morning.");
        assertThat(recommendation.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 6, 9, 30));
        assertThat(recommendation.getPlantingInformation()).isEqualTo(plantingInformation);
    }

    @Test
    @DisplayName("Should initialize forecasts list by default")
    void shouldInitializeForecastsListByDefault() {
        Recommendation recommendation = new Recommendation();

        assertThat(recommendation.getForecasts()).isNotNull();
        assertThat(recommendation.getForecasts()).isEmpty();
    }
}