package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.FarmingStatsDto;
import com.timmk22.smartfarming.dto.GeneratePdfRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Locale;

@Service
public class HeuristicAiHealthSummaryService implements HealthSummaryService {

    @Override
    public String generateSummary(GeneratePdfRequest request) {
        int totalFields = request.getStats().size();

        double avgDiseaseRisk = request.getStats().stream()
                .mapToDouble(FarmingStatsDto::getDiseaseRiskScore)
                .average()
                .orElse(0.0);

        double avgMoisture = request.getStats().stream()
                .mapToDouble(FarmingStatsDto::getSoilMoisturePct)
                .average()
                .orElse(0.0);

        int totalPestIncidents = request.getStats().stream()
                .mapToInt(FarmingStatsDto::getPestIncidents)
                .sum();

        FarmingStatsDto weakestField = request.getStats().stream()
                .max(Comparator.comparingDouble(FarmingStatsDto::getDiseaseRiskScore)
                        .thenComparingInt(FarmingStatsDto::getPestIncidents))
                .orElse(null);

        String riskBand = getRiskBand(avgDiseaseRisk, totalPestIncidents);

        StringBuilder summary = new StringBuilder();
        summary.append("The AI health model reviewed ")
                .append(totalFields)
                .append(" fields and classifies the overall farm condition as ")
                .append(riskBand)
                .append(". ");

        summary.append("Average disease risk is ")
                .append(formatNumber(avgDiseaseRisk))
                .append("/100 with average soil moisture at ")
                .append(formatNumber(avgMoisture))
                .append("%. ");

        summary.append("Total recorded pest incidents are ")
                .append(totalPestIncidents)
                .append(". ");

        if (weakestField != null) {
            summary.append("Priority intervention is recommended for field ")
                    .append(weakestField.getFieldName())
                    .append(" (crop: ")
                    .append(weakestField.getCropName())
                    .append(") due to elevated disease risk and/or pest pressure. ");
        }

        summary.append("Suggested actions: targeted scouting every 48 hours, irrigation balancing for moisture stability, and preventive treatment review with agronomy staff.");

        return summary.toString();
    }

    private String getRiskBand(double avgDiseaseRisk, int totalPestIncidents) {
        if (avgDiseaseRisk >= 70 || totalPestIncidents >= 10) {
            return "high risk";
        }
        if (avgDiseaseRisk >= 40 || totalPestIncidents >= 4) {
            return "moderate risk";
        }
        return "stable";
    }

    private String formatNumber(double value) {
        return String.format(Locale.US, "%.1f", value);
    }
}

