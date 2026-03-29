package com.timmk22.smartfarming.repository;

import com.timmk22.smartfarming.model.PlantingInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantingInformationRepository extends JpaRepository<PlantingInformation, Long> {

    List<PlantingInformation> findByUserUserId(Long userId);

    List<PlantingInformation> findByCropCropId(Long cropId);

    List<PlantingInformation> findBySoilTypeSoilId(Long soilId);
}

