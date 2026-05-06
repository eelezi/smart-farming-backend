package com.timmk22.smartfarming.repository;

import com.timmk22.smartfarming.model.Crop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class CropRepositoryTest {

    @Autowired
    private CropRepository cropRepository;

    private Crop createCrop(String name) {
        Crop crop = new Crop();
        crop.setName(name);
        return crop;
    }

    @Test
    @DisplayName("Should save crop and find by id")
    void shouldSaveCropAndFindById() {
        Crop crop = createCrop("Tomato");

        Crop saved = cropRepository.saveAndFlush(crop);
        Optional<Crop> found = cropRepository.findById(saved.getCropId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Tomato");
    }

    @Test
    @DisplayName("Should find crop by name ignoring case")
    void shouldFindByNameIgnoreCase() {
        Crop crop = createCrop("Tomato");
        cropRepository.saveAndFlush(crop);

        Optional<Crop> found = cropRepository.findByNameIgnoreCase("tOmAtO");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Tomato");
    }

    @Test
    @DisplayName("Should delete crop")
    void shouldDeleteCrop() {
        Crop crop = createCrop("Pepper");
        Crop saved = cropRepository.saveAndFlush(crop);

        cropRepository.deleteById(saved.getCropId());
        cropRepository.flush();

        assertThat(cropRepository.findById(saved.getCropId())).isEmpty();
    }

    @Test
    @DisplayName("Should enforce unique crop name")
    void shouldEnforceUniqueCropName() {
        cropRepository.saveAndFlush(createCrop("Corn"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            cropRepository.saveAndFlush(createCrop("Corn"));
        });
    }
}