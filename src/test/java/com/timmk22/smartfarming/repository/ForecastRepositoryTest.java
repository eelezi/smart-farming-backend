package com.timmk22.smartfarming.repository;

import com.timmk22.smartfarming.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ForecastRepositoryTest {

    @Autowired
    private ForecastRepository forecastRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private PlantingInformationRepository plantingInformationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private SoilTypeRepository soilTypeRepository;

    private User createUser() {
        User user = new User();
        user.setName("Dimitar");
        user.setEmail("forecast-user@test.com");
        user.setPassword("123456");
        return userRepository.saveAndFlush(user);
    }

    private Crop createCrop() {
        Crop crop = new Crop();
        crop.setName("Tomato-forecast");
        return cropRepository.saveAndFlush(crop);
    }

    private SoilType createSoilType() {
        SoilType soilType = new SoilType();
        soilType.setName("Loamy-forecast");
        return soilTypeRepository.saveAndFlush(soilType);
    }

    private PlantingInformation createPlantingInformation(User user, Crop crop, SoilType soilType) {
        PlantingInformation planting = new PlantingInformation();
        planting.setArea(10.0);
        planting.setPlantingDate(LocalDate.of(2026, 5, 1));
        planting.setCurrentStatus(com.timmk22.smartfarming.enumeration.CurrentStatus.HEALTHY);
        planting.setUser(user);
        planting.setCrop(crop);
        planting.setSoilType(soilType);
        return plantingInformationRepository.saveAndFlush(planting);
    }

    private Recommendation createRecommendation(PlantingInformation planting) {
        Recommendation recommendation = new Recommendation();
        recommendation.setRecommendationText("Water crops tomorrow");
        recommendation.setCreatedAt(LocalDateTime.of(2026, 5, 6, 9, 0));
        recommendation.setPlantingInformation(planting);
        return recommendationRepository.saveAndFlush(recommendation);
    }


    private Forecast createForecast(Recommendation recommendation) {
        Forecast forecast = new Forecast();
        forecast.setRecommendation(recommendation);
        forecast.setLatitude(new BigDecimal("41.123456"));
        forecast.setLongitude(new BigDecimal("22.654321"));
        forecast.setTimezone("Europe/Skopje");
        forecast.setForecastDays(7);
        forecast.setTime(LocalDateTime.of(2026, 5, 6, 10, 0));
        forecast.setTemp2mMax(new BigDecimal("28.50"));
        forecast.setTemp2mMin(new BigDecimal("14.20"));
        forecast.setSunrise(LocalDateTime.of(2026, 5, 6, 5, 10));
        forecast.setSunset(LocalDateTime.of(2026, 5, 6, 19, 30));
        forecast.setPercProbMax(new BigDecimal("60.00"));
        forecast.setRainSum(new BigDecimal("12.30"));
        forecast.setShowersSum(new BigDecimal("3.40"));
        forecast.setSnowfallSum(new BigDecimal("0.00"));
        return forecast;
    }

    @Test
    @DisplayName("Should save forecast and find by id")
    void shouldSaveForecastAndFindById() {
        User user = createUser();
        Crop crop = createCrop();
        SoilType soilType = createSoilType();
        PlantingInformation planting = createPlantingInformation(user, crop, soilType);
        Recommendation recommendation = createRecommendation(planting);

        Forecast forecast = createForecast(recommendation);
        Forecast saved = forecastRepository.saveAndFlush(forecast);

        Optional<Forecast> found = forecastRepository.findById(saved.getForecastId());

        assertThat(found).isPresent();
        assertThat(found.get().getTimezone()).isEqualTo("Europe/Skopje");
    }

    @Test
    @DisplayName("Should find forecasts by recommendation id")
    void shouldFindByRecommendationRecommendationId() {
        User user = createUser();
        Crop crop = createCrop();
        SoilType soilType = createSoilType();
        PlantingInformation planting = createPlantingInformation(user, crop, soilType);
        Recommendation recommendation = createRecommendation(planting);

        forecastRepository.saveAndFlush(createForecast(recommendation));

        List<Forecast> result = forecastRepository.findByRecommendationRecommendationId(
                recommendation.getRecommendationId()
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecommendation().getRecommendationId())
                .isEqualTo(recommendation.getRecommendationId());
    }

    @Test
    @DisplayName("Should delete forecast")
    void shouldDeleteForecast() {
        User user = createUser();
        Crop crop = createCrop();
        SoilType soilType = createSoilType();
        PlantingInformation planting = createPlantingInformation(user, crop, soilType);
        Recommendation recommendation = createRecommendation(planting);

        Forecast saved = forecastRepository.saveAndFlush(createForecast(recommendation));

        forecastRepository.deleteById(saved.getForecastId());
        forecastRepository.flush();

        assertThat(forecastRepository.findById(saved.getForecastId())).isEmpty();
    }

    @Test
    @DisplayName("Should enforce non null recommendation")
    void shouldEnforceNonNullRecommendation() {
        Forecast forecast = new Forecast();
        forecast.setTimezone("Europe/Skopje");

        assertThrows(DataIntegrityViolationException.class, () -> {
            forecastRepository.saveAndFlush(forecast);
        });
    }
}