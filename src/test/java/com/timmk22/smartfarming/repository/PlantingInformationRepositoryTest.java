package com.timmk22.smartfarming.repository;

import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.enumeration.IrrigationType;
import com.timmk22.smartfarming.model.Crop;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.model.SoilType;
import com.timmk22.smartfarming.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class PlantingInformationRepositoryTest {

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

    private Crop createCrop() {
        Crop crop = new Crop();
        crop.setName("Tomato");
        return cropRepository.saveAndFlush(crop);
    }

    private SoilType createSoilType() {
        SoilType soilType = new SoilType();
        soilType.setName("Loamy");
        return soilTypeRepository.saveAndFlush(soilType);
    }

    private PlantingInformation createPlanting(User user, Crop crop, SoilType soilType) {
        PlantingInformation planting = new PlantingInformation();
        planting.setArea(12.5);
        planting.setLatitude(41.63);
        planting.setLongitude(22.47);
        planting.setLocationName("Radovis");
        planting.setIrrigationType(IrrigationType.DRIP);
        planting.setCurrentStatus(CurrentStatus.HEALTHY);
        planting.setPlantingDate(LocalDate.of(2026, 5, 1));
        planting.setExpectedHarvestDate(LocalDate.of(2026, 9, 1));
        planting.setNotes("Test planting");
        planting.setUser(user);
        planting.setCrop(crop);
        planting.setSoilType(soilType);
        return planting;
    }

    @Test
    @DisplayName("Should save and find planting by id")
    void shouldSaveAndFindById() {
        User user = createUser("Dimitar", "dimitar@planting.com");
        Crop crop = createCrop();
        SoilType soilType = createSoilType();

        PlantingInformation planting = createPlanting(user, crop, soilType);
        PlantingInformation saved = plantingInformationRepository.saveAndFlush(planting);

        assertThat(saved.getPlantingId()).isNotNull();
        assertThat(plantingInformationRepository.findById(saved.getPlantingId())).isPresent();
    }

    @Test
    @DisplayName("Should find plantings by user userId")
    void shouldFindByUserUserId() {
        User user = createUser("Dimitar", "userquery@test.com");
        Crop crop = createCrop();
        SoilType soilType = createSoilType();

        plantingInformationRepository.saveAndFlush(createPlanting(user, crop, soilType));

        List<PlantingInformation> result = plantingInformationRepository.findByUserUserId(user.getUserId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("Should find plantings by crop cropId")
    void shouldFindByCropCropId() {
        User user = createUser("Dimitar", "cropquery@test.com");
        Crop crop = createCrop();
        SoilType soilType = createSoilType();

        plantingInformationRepository.saveAndFlush(createPlanting(user, crop, soilType));

        List<PlantingInformation> result = plantingInformationRepository.findByCropCropId(crop.getCropId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCrop().getCropId()).isEqualTo(crop.getCropId());
    }

    @Test
    @DisplayName("Should find plantings by soil type soilId")
    void shouldFindBySoilTypeSoilId() {
        User user = createUser("Dimitar", "soilquery@test.com");
        Crop crop = createCrop();
        SoilType soilType = createSoilType();

        plantingInformationRepository.saveAndFlush(createPlanting(user, crop, soilType));

        List<PlantingInformation> result = plantingInformationRepository.findBySoilTypeSoilId(soilType.getSoilId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSoilType().getSoilId()).isEqualTo(soilType.getSoilId());
    }

    @Test
    @DisplayName("Should delete planting")
    void shouldDeletePlanting() {
        User user = createUser("Dimitar", "deleteplanting@test.com");
        Crop crop = createCrop();
        SoilType soilType = createSoilType();

        PlantingInformation saved = plantingInformationRepository
                .saveAndFlush(createPlanting(user, crop, soilType));

        plantingInformationRepository.deleteById(saved.getPlantingId());
        plantingInformationRepository.flush();

        assertThat(plantingInformationRepository.findById(saved.getPlantingId())).isEmpty();
    }

    @Test
    @DisplayName("Should enforce non null user")
    void shouldEnforceNonNullUser() {
        Crop crop = createCrop();
        SoilType soilType = createSoilType();

        PlantingInformation planting = new PlantingInformation();
        planting.setArea(12.5);
        planting.setCurrentStatus(CurrentStatus.HEALTHY);
        planting.setPlantingDate(LocalDate.of(2026, 5, 1));
        planting.setCrop(crop);
        planting.setSoilType(soilType);

        assertThrows(DataIntegrityViolationException.class, () ->
                plantingInformationRepository.saveAndFlush(planting)
        );
    }
}