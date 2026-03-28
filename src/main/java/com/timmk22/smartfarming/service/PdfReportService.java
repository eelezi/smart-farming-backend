package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.GeneratePdfRequest;

public interface PdfReportService {

    byte[] generateReport(GeneratePdfRequest request);
}

