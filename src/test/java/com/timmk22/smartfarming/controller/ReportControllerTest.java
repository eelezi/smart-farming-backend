package com.timmk22.smartfarming.controller;

import com.timmk22.smartfarming.dto.FarmingStatsDto;
import com.timmk22.smartfarming.dto.GeneratePdfRequest;
import com.timmk22.smartfarming.service.PdfReportService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportControllerTest {

    @Test
    void generatePdfShouldReturnPdfResponse() {
        PdfReportService pdfReportService = request -> "pdf-data".getBytes();
        ReportController controller = new ReportController(pdfReportService);

        GeneratePdfRequest request = validRequest();

        ResponseEntity<byte[]> response = controller.generatePdf(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().toString()).isEqualTo("application/pdf");
        assertThat(response.getHeaders().getFirst("Content-Disposition"))
                .isEqualTo("attachment; filename=\"farming-report-2026-03-28.pdf\"");
        assertThat(response.getBody()).isNotNull();
    }

    private GeneratePdfRequest validRequest() {
        FarmingStatsDto stats = new FarmingStatsDto();
        stats.setFieldName("F-01");
        stats.setCropName("Corn");
        stats.setAcreage(11.0);
        stats.setYieldPerAcre(4.5);
        stats.setSoilMoisturePct(48.0);
        stats.setPestIncidents(1);
        stats.setDiseaseRiskScore(25.0);

        GeneratePdfRequest request = new GeneratePdfRequest();
        request.setFarmName("North Farm");
        request.setReportDate(LocalDate.of(2026, 3, 28));
        request.setPreparedBy("Engineer");
        request.setStats(List.of(stats));
        return request;
    }
}
