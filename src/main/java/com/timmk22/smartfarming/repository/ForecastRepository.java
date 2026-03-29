package com.timmk22.smartfarming.repository;

import com.timmk22.smartfarming.model.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForecastRepository extends JpaRepository<Forecast, Long> {

    List<Forecast> findByRecommendationRecommendationId(Long recommendationId);
}

