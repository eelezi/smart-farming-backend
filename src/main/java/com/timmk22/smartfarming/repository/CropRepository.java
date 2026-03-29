package com.timmk22.smartfarming.repository;

import com.timmk22.smartfarming.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {

    Optional<Crop> findByNameIgnoreCase(String name);
}

