package com.timmk22.smartfarming.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CropEntityTest {

    @Test
    @DisplayName("Should create crop and set fields")
    void shouldCreateCropAndSetFields() {
        Crop crop = new Crop();

        crop.setCropId(1L);
        crop.setName("Tomato");

        assertThat(crop.getCropId()).isEqualTo(1L);
        assertThat(crop.getName()).isEqualTo("Tomato");
    }

    @Test
    @DisplayName("Should initialize plantings list by default")
    void shouldInitializePlantingsListByDefault() {
        Crop crop = new Crop();

        assertThat(crop.getPlantings()).isNotNull();
        assertThat(crop.getPlantings()).isEmpty();
    }
}