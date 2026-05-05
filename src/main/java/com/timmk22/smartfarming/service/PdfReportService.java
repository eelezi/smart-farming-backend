package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.model.PlantingInformation;

import java.util.List;

public interface PdfReportService {

    /**
     * Generate a PDF report from planting information records stored in the database.
     * This method is called when user generates report from crop details page.
     *
     * @param plantings list of PlantingInformation records to include in the report
     * @param farmName farm name for the report header
     * @param reportDate date when the report was generated
     * @param preparedBy name of the person who prepared the report
     * @return PDF report as byte array
     */
    byte[] generateReportFromDatabase(List<PlantingInformation> plantings, String farmName, String reportDate, String preparedBy);
}

