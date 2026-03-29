package com.timmk22.smartfarming.repository;

import com.timmk22.smartfarming.model.SoilType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SoilTypeRepository extends JpaRepository<SoilType, Long> {

    Optional<SoilType> findByNameIgnoreCase(String name);
}

