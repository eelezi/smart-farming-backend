package com.timmk22.smartfarming.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class FarmingStatsDto {

    @NotBlank
    private String fieldName;

    @NotBlank
    private String cropName;

    @Positive
    private double acreage;

    @PositiveOrZero
    private double yieldPerAcre;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double soilMoisturePct;

    @PositiveOrZero
    private int pestIncidents;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double diseaseRiskScore;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public double getAcreage() {
        return acreage;
    }

    public void setAcreage(double acreage) {
        this.acreage = acreage;
    }

    public double getYieldPerAcre() {
        return yieldPerAcre;
    }

    public void setYieldPerAcre(double yieldPerAcre) {
        this.yieldPerAcre = yieldPerAcre;
    }

    public double getSoilMoisturePct() {
        return soilMoisturePct;
    }

    public void setSoilMoisturePct(double soilMoisturePct) {
        this.soilMoisturePct = soilMoisturePct;
    }

    public int getPestIncidents() {
        return pestIncidents;
    }

    public void setPestIncidents(int pestIncidents) {
        this.pestIncidents = pestIncidents;
    }

    public double getDiseaseRiskScore() {
        return diseaseRiskScore;
    }

    public void setDiseaseRiskScore(double diseaseRiskScore) {
        this.diseaseRiskScore = diseaseRiskScore;
    }
}

