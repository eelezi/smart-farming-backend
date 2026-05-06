package com.timmk22.smartfarming.repository;

import com.timmk22.smartfarming.model.SoilType;
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
class SoilTypeRepositoryTest {

    @Autowired
    private SoilTypeRepository soilTypeRepository;

    private SoilType createSoilType(String name) {
        SoilType soilType = new SoilType();
        soilType.setName(name);
        return soilType;
    }

    @Test
    @DisplayName("Should save soil type and find by id")
    void shouldSaveSoilTypeAndFindById() {
        SoilType soilType = createSoilType("Loamy");

        SoilType saved = soilTypeRepository.saveAndFlush(soilType);
        Optional<SoilType> found = soilTypeRepository.findById(saved.getSoilId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Loamy");
    }

    @Test
    @DisplayName("Should find soil type by name ignoring case")
    void shouldFindByNameIgnoreCase() {
        soilTypeRepository.saveAndFlush(createSoilType("Clay"));

        Optional<SoilType> found = soilTypeRepository.findByNameIgnoreCase("cLaY");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Clay");
    }

    @Test
    @DisplayName("Should delete soil type")
    void shouldDeleteSoilType() {
        SoilType soilType = createSoilType("Sandy");
        SoilType saved = soilTypeRepository.saveAndFlush(soilType);

        soilTypeRepository.deleteById(saved.getSoilId());
        soilTypeRepository.flush();

        assertThat(soilTypeRepository.findById(saved.getSoilId())).isEmpty();
    }

    @Test
    @DisplayName("Should enforce unique soil type name")
    void shouldEnforceUniqueSoilTypeName() {
        soilTypeRepository.saveAndFlush(createSoilType("Peaty"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            soilTypeRepository.saveAndFlush(createSoilType("Peaty"));
        });
    }
}