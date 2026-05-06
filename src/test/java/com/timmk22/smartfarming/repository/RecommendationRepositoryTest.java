package com.timmk22.smartfarming.repository;

import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.model.Crop;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.model.Recommendation;
import com.timmk22.smartfarming.model.SoilType;
import com.timmk22.smartfarming.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class RecommendationRepositoryTest {

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

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("123456");
        return userRepository.saveAndFlush(user);
    }

    private Crop createCrop(String name) {
        Crop crop = new Crop();
        crop.setName(name);
        return cropRepository.saveAndFlush(crop);
    }

    private SoilType createSoilType(String name) {
        SoilType soilType = new SoilType();
        soilType.setName(name);
        return soilTypeRepository.saveAndFlush(soilType);
    }

    private PlantingInformation createPlanting(User user, Crop crop, SoilType soilType) {
        PlantingInformation planting = new PlantingInformation();
        planting.setArea(12.5);
        planting.setPlantingDate(LocalDate.of(2026, 5, 1));
        planting.setCurrentStatus(CurrentStatus.HEALTHY);
        planting.setUser(user);
        planting.setCrop(crop);
        planting.setSoilType(soilType);
        return plantingInformationRepository.saveAndFlush(planting);
    }

    private Recommendation createRecommendation(PlantingInformation planting) {
        Recommendation recommendation = new Recommendation();
        recommendation.setRecommendationText("Irrigate tomorrow morning.");
        recommendation.setCreatedAt(LocalDateTime.of(2026, 5, 6, 9, 30));
        recommendation.setPlantingInformation(planting);
        return recommendationRepository.saveAndFlush(recommendation);
    }

    @Test
    @DisplayName("Should save recommendation and find by id")
    void shouldSaveRecommendationAndFindById() {
        User user = createUser("Dimitar", "recommendation1@test.com");
        Crop crop = createCrop("Tomato-rec-1");
        SoilType soilType = createSoilType("Loamy-rec-1");
        PlantingInformation planting = createPlanting(user, crop, soilType);

        Recommendation saved = createRecommendation(planting);
        Optional<Recommendation> found = recommendationRepository.findById(saved.getRecommendationId());

        assertThat(found).isPresent();
        assertThat(found.get().getRecommendationText()).isEqualTo("Irrigate tomorrow morning.");
    }

    @Test
    @DisplayName("Should find recommendations by planting information plantingId")
    void shouldFindByPlantingInformationPlantingId() {
        User user = createUser("Dimitar", "recommendation2@test.com");
        Crop crop = createCrop("Tomato-rec-2");
        SoilType soilType = createSoilType("Loamy-rec-2");
        PlantingInformation planting = createPlanting(user, crop, soilType);

        createRecommendation(planting);

        List<Recommendation> result =
                recommendationRepository.findByPlantingInformationPlantingId(planting.getPlantingId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlantingInformation().getPlantingId())
                .isEqualTo(planting.getPlantingId());
    }

    @Test
    @DisplayName("Should delete recommendation")
    void shouldDeleteRecommendation() {
        User user = createUser("Dimitar", "recommendation3@test.com");
        Crop crop = createCrop("Tomato-rec-3");
        SoilType soilType = createSoilType("Loamy-rec-3");
        PlantingInformation planting = createPlanting(user, crop, soilType);

        Recommendation saved = createRecommendation(planting);

        recommendationRepository.deleteById(saved.getRecommendationId());
        recommendationRepository.flush();

        assertThat(recommendationRepository.findById(saved.getRecommendationId())).isEmpty();
    }

    @Test
    @DisplayName("Should enforce non null planting information")
    void shouldEnforceNonNullPlantingInformation() {
        Recommendation recommendation = new Recommendation();
        recommendation.setRecommendationText("Use moderate irrigation.");
        recommendation.setCreatedAt(LocalDateTime.of(2026, 5, 6, 9, 30));
        recommendation.setPlantingInformation(null);

        assertThrows(DataIntegrityViolationException.class, () ->
                recommendationRepository.saveAndFlush(recommendation)
        );
    }
}