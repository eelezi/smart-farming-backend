package com.timmk22.smartfarming.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class GeneratePdfRequest {

    @NotBlank
    private String farmName;

    @NotNull
    private LocalDate reportDate;

    @NotBlank
    private String preparedBy;

    @NotEmpty
    @Valid
    private List<FarmingStatsDto> stats;

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(String preparedBy) {
        this.preparedBy = preparedBy;
    }

    public List<FarmingStatsDto> getStats() {
        return stats;
    }

    public void setStats(List<FarmingStatsDto> stats) {
        this.stats = stats;
    }
}

