package com.timmk22.smartfarming.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmk22.smartfarming.dto.FarmingStatsDto;
import com.timmk22.smartfarming.dto.GeneratePdfRequest;
import com.timmk22.smartfarming.service.PdfReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PdfReportService pdfReportService;

    @Test
    void generatePdfShouldReturnPdfResponse() throws Exception {
        when(pdfReportService.generateReport(any())).thenReturn("pdf-data".getBytes());

        GeneratePdfRequest request = validRequest();

        mockMvc.perform(post("/generate-pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"farming-report-2026-03-28.pdf\""));
    }

    @Test
    void generatePdfShouldRejectInvalidPayload() throws Exception {
        GeneratePdfRequest request = new GeneratePdfRequest();
        request.setFarmName("");

        mockMvc.perform(post("/generate-pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
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

