package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.GeneratePdfRequest;
import com.timmk22.smartfarming.service.PdfReportService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    private final PdfReportService pdfReportService;

    public ReportController(PdfReportService pdfReportService) {
        this.pdfReportService = pdfReportService;
    }

    @PostMapping(value = "/generate-pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(@Valid @RequestBody GeneratePdfRequest request) {
        byte[] pdfBytes = pdfReportService.generateReport(request);

        String fileName = "farming-report-" + request.getReportDate() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(fileName)
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}

