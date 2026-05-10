package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.enumeration.IrrigationType;
import com.timmk22.smartfarming.model.Crop;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.model.SoilType;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.service.impl.PdfReportServiceImpl;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfReportServiceImplTest {

    /**
     * Test that PDF is generated correctly from PlantingInformation database records.
     * Verifies that all fields from the database are included without computed values.
     */
    @Test
    void generateReportFromDatabaseShouldIncludeOnlyDatabaseFields() throws IOException {
        HealthSummaryService summaryService = (farmName, plantings) -> "AI analysis of farm conditions.";
        PdfReportService service = new PdfReportServiceImpl(summaryService, "Test Farming Co");

        // Create PlantingInformation records
        List<PlantingInformation> plantings = List.of(
                buildSamplePlanting("Field-A", "Wheat", 50.0, LocalDate.of(2026, 1, 15)),
                buildSamplePlanting("Field-B", "Corn", 35.0, LocalDate.of(2026, 2, 1))
        );

        byte[] pdf = service.generateReportFromDatabase(
                plantings,
                "Test Farm",
                "2026-05-05",
                "System User"
        );

        assertThat(pdf).isNotEmpty();

        try (PDDocument document = PDDocument.load(pdf)) {
            String text = new PDFTextStripper().getText(document);
            assertThat(text).contains("Farming Health Report");
            assertThat(text).contains("Test Farm");
            assertThat(text).contains("System User");
            assertThat(text).contains("Wheat");
            assertThat(text).contains("Corn");
            assertThat(text).contains("Field-A");
            assertThat(text).contains("Field-B");
            assertThat(text).contains("Planting Information");
            // Verify no computed fields in header
            assertThat(text).doesNotContain("Yield/Acr");
            assertThat(text).doesNotContain("Moisture%");
            assertThat(text).doesNotContain("Pests");
            assertThat(text).doesNotContain("Risk");
        }
    }

    /**
     * Build a sample PlantingInformation for testing database-driven PDF generation.
     */
    private PlantingInformation buildSamplePlanting(String locationName, String cropName, double area, LocalDate plantingDate) {
        PlantingInformation planting = new PlantingInformation();
        planting.setPlantingId(1L);
        planting.setLocationName(locationName);
        planting.setArea(area);
        planting.setPlantingDate(plantingDate);
        planting.setExpectedHarvestDate(plantingDate.plusMonths(4));
        planting.setCurrentStatus(CurrentStatus.HEALTHY);
        planting.setIrrigationType(IrrigationType.SPRINKLER);

        // Set crop
        Crop crop = new Crop();
        crop.setCropId(1L);
        crop.setName(cropName);
        planting.setCrop(crop);

        // Set soil type
        SoilType soilType = new SoilType();
        soilType.setSoilId(1L);
        soilType.setName("Loamy");
        planting.setSoilType(soilType);

        // Set user
        User user = new User();
        user.setUserId(1L);
        user.setName("testuser");
        user.setEmail("test@example.com");
        planting.setUser(user);

        return planting;
    }
}

