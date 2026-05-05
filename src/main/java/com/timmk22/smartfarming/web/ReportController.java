package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.repository.PlantingInformationRepository;
import com.timmk22.smartfarming.service.PdfReportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ReportController {

    private final PdfReportService pdfReportService;
    private final PlantingInformationRepository plantingInformationRepository;

    public ReportController(PdfReportService pdfReportService, PlantingInformationRepository plantingInformationRepository) {
        this.pdfReportService = pdfReportService;
        this.plantingInformationRepository = plantingInformationRepository;
    }

    /**
     * Generate PDF from database for a specific crop.
     * Called from crop details page - generates report based on all planting records for that crop.
     * Only includes planting records belonging to the authenticated user.
     *
     * @param cropId the crop ID
     * @param user   the authenticated user
     * @return PDF file as attachment
     */
    @GetMapping(value = "/generate-pdf/crop/{cropId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdfFromCrop(
            @PathVariable Long cropId,
            @AuthenticationPrincipal User user
    ) {
        try {
            // Fetch all plantings for the given crop
            List<PlantingInformation> allPlantings = plantingInformationRepository.findByCropCropId(cropId);

            // Filter only user's plantings (security check)
            List<PlantingInformation> userPlantings = allPlantings.stream()
                    .filter(p -> p.getUser().getUserId().equals(user.getUserId()))
                    .toList();

            if (userPlantings.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Get crop name from first planting
            String cropName = userPlantings.get(0).getCrop().getName();
            String farmName = cropName + " Report";
            String reportDate = LocalDate.now().toString();
            String preparedBy = user.getUsername() != null ? user.getUsername() : "User";

            byte[] pdfBytes = pdfReportService.generateReportFromDatabase(
                    userPlantings,
                    farmName,
                    reportDate,
                    preparedBy
            );

            String fileName = "crop-" + cropName + "-report-" + reportDate + ".pdf";

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

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

