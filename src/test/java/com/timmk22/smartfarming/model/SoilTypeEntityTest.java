package com.timmk22.smartfarming.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SoilTypeEntityTest {

    @Test
    @DisplayName("Should create soil type and set fields")
    void shouldCreateSoilTypeAndSetFields() {
        SoilType soilType = new SoilType();

        soilType.setSoilId(1L);
        soilType.setName("Loamy");

        assertThat(soilType.getSoilId()).isEqualTo(1L);
        assertThat(soilType.getName()).isEqualTo("Loamy");
    }

    @Test
    @DisplayName("Should initialize plantings list by default")
    void shouldInitializePlantingsListByDefault() {
        SoilType soilType = new SoilType();

        assertThat(soilType.getPlantings()).isNotNull();
        assertThat(soilType.getPlantings()).isEmpty();
    }
}