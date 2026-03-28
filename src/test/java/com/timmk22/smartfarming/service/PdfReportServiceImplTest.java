package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.FarmingStatsDto;
import com.timmk22.smartfarming.dto.GeneratePdfRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfReportServiceImplTest {

    @Test
    void generateReportShouldIncludeReportSections() throws IOException {
        HealthSummaryService summaryService = request -> "AI summary text for testing.";
        PdfReportService service = new PdfReportServiceImpl(summaryService, "Test Farming Co");

        GeneratePdfRequest request = new GeneratePdfRequest();
        request.setFarmName("North Farm");
        request.setReportDate(LocalDate.of(2026, 3, 28));
        request.setPreparedBy("QA Engineer");
        request.setStats(List.of(buildSampleStats()));

        byte[] pdf = service.generateReport(request);

        assertThat(pdf).isNotEmpty();

        try (PDDocument document = PDDocument.load(pdf)) {
            String text = new PDFTextStripper().getText(document);
            assertThat(text).contains("Farming Health Report");
            assertThat(text).contains("AI-Generated Health Summary");
            assertThat(text).contains("AI summary text for testing.");
            assertThat(text).contains("North Farm");
        }
    }

    private FarmingStatsDto buildSampleStats() {
        FarmingStatsDto stats = new FarmingStatsDto();
        stats.setFieldName("F-01");
        stats.setCropName("Corn");
        stats.setAcreage(12.5);
        stats.setYieldPerAcre(6.75);
        stats.setSoilMoisturePct(45.1);
        stats.setPestIncidents(2);
        stats.setDiseaseRiskScore(31.4);
        return stats;
    }
}

