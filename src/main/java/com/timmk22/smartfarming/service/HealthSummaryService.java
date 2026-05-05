package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.model.PlantingInformation;

import java.util.List;

public interface HealthSummaryService {

    String generateSummary(String farmName, List<PlantingInformation> plantings);
}

