package com.timmk22.smartfarming.model;

import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.enumeration.IrrigationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PlantingInformationEntityTest {

    @Test
    @DisplayName("Should create planting information and set fields")
    void shouldCreatePlantingInformationAndSetFields() {
        User user = new User();
        user.setUserId(1L);

        Crop crop = new Crop();
        crop.setCropId(2L);

        SoilType soilType = new SoilType();
        soilType.setSoilId(3L);

        PlantingInformation planting = new PlantingInformation();
        planting.setPlantingId(10L);
        planting.setArea(15.5);
        planting.setLatitude(41.63);
        planting.setLongitude(22.47);
        planting.setLocationName("Radovis");
        planting.setIrrigationType(IrrigationType.DRIP);
        planting.setCurrentStatus(CurrentStatus.HEALTHY);
        planting.setPlantingDate(LocalDate.of(2026, 5, 1));
        planting.setExpectedHarvestDate(LocalDate.of(2026, 9, 1));
        planting.setNotes("Healthy crop progress");
        planting.setUser(user);
        planting.setCrop(crop);
        planting.setSoilType(soilType);

        assertThat(planting.getPlantingId()).isEqualTo(10L);
        assertThat(planting.getArea()).isEqualTo(15.5);
        assertThat(planting.getLatitude()).isEqualTo(41.63);
        assertThat(planting.getLongitude()).isEqualTo(22.47);
        assertThat(planting.getLocationName()).isEqualTo("Radovis");
        assertThat(planting.getIrrigationType()).isEqualTo(IrrigationType.DRIP);
        assertThat(planting.getCurrentStatus()).isEqualTo(CurrentStatus.HEALTHY);
        assertThat(planting.getPlantingDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(planting.getExpectedHarvestDate()).isEqualTo(LocalDate.of(2026, 9, 1));
        assertThat(planting.getNotes()).isEqualTo("Healthy crop progress");
        assertThat(planting.getUser()).isEqualTo(user);
        assertThat(planting.getCrop()).isEqualTo(crop);
        assertThat(planting.getSoilType()).isEqualTo(soilType);
    }

    @Test
    @DisplayName("Should initialize recommendations list by default")
    void shouldInitializeRecommendationsListByDefault() {
        PlantingInformation planting = new PlantingInformation();

        assertThat(planting.getRecommendations()).isNotNull();
        assertThat(planting.getRecommendations()).isEmpty();
    }

    @Test
    @DisplayName("Should have default current status HEALTHY")
    void shouldHaveDefaultCurrentStatusHealthy() {
        PlantingInformation planting = new PlantingInformation();

        assertThat(planting.getCurrentStatus()).isEqualTo(CurrentStatus.HEALTHY);
    }
}