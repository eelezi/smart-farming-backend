package com.timmk22.smartfarming.controller;

import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.enumeration.IrrigationType;
import com.timmk22.smartfarming.model.Crop;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.model.SoilType;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.repository.PlantingInformationRepository;
import com.timmk22.smartfarming.service.PdfReportService;
import com.timmk22.smartfarming.web.ReportController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReportControllerTest {

    /**
     * Test GET endpoint: generating PDF from database records for a crop.
     * Simulates user accessing crop details page and clicking "generate-pdf" button.
     */
    @Test
    void generatePdfFromCropShouldReturnPdfWithUserPlantings() {
        // Setup mocks
        PdfReportService pdfReportService = mock(PdfReportService.class);
        when(pdfReportService.generateReportFromDatabase(anyList(), anyString(), anyString(), anyString()))
                .thenReturn("pdf-from-db".getBytes());

        PlantingInformationRepository mockRepository = mock(PlantingInformationRepository.class);

        // Create test user
        User user = new User();
        user.setUserId(1L);
        user.setName("John Farmer");
        user.setEmail("farmer@example.com");

        // Create crop
        Crop crop = new Crop();
        crop.setCropId(100L);
        crop.setName("Wheat");

        // Create plantings for the user
        List<PlantingInformation> plantings = List.of(
                buildPlanting(1L, user, crop, "Field-A", 50.0),
                buildPlanting(2L, user, crop, "Field-B", 35.0)
        );

        when(mockRepository.findByCropCropId(100L)).thenReturn(plantings);

        ReportController controller = new ReportController(pdfReportService, mockRepository);

        // Execute
        ResponseEntity<byte[]> response = controller.generatePdfFromCrop(100L, user);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().toString()).isEqualTo("application/pdf");
        assertThat(response.getBody()).isNotNull();

        // Verify that generateReportFromDatabase was called with user's plantings
        verify(pdfReportService, times(1)).generateReportFromDatabase(
                argThat(list -> list.size() == 2),
                contains("Wheat"),
                anyString(),
                eq("farmer@example.com")
        );
    }

    /**
     * Test GET endpoint: should return 404 when user has no plantings for the crop.
     */
    @Test
    void generatePdfFromCropShouldReturn404WhenNoUserPlantings() {
        PdfReportService pdfReportService = mock(PdfReportService.class);
        PlantingInformationRepository mockRepository = mock(PlantingInformationRepository.class);

        User user = new User();
        user.setUserId(1L);
        user.setName("John Farmer");
        user.setEmail("farmer@example.com");

        // Setup plantings from different user
        User otherUser = new User();
        otherUser.setUserId(2L);
        otherUser.setName("Other Farmer");
        otherUser.setEmail("other@example.com");

        Crop crop = new Crop();
        crop.setCropId(100L);
        crop.setName("Wheat");

        PlantingInformation otherUserPlanting = buildPlanting(1L, otherUser, crop, "Field-A", 50.0);
        when(mockRepository.findByCropCropId(100L)).thenReturn(List.of(otherUserPlanting));

        ReportController controller = new ReportController(pdfReportService, mockRepository);

        // Execute - user tries to access crop they don't have plantings for
        ResponseEntity<byte[]> response = controller.generatePdfFromCrop(100L, user);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Test GET endpoint: return 404 when crop has no plantings at all.
     */
    @Test
    void generatePdfFromCropShouldReturn404WhenCropHasNoPlantings() {
        PdfReportService pdfReportService = mock(PdfReportService.class);
        PlantingInformationRepository mockRepository = mock(PlantingInformationRepository.class);

        User user = new User();
        user.setUserId(1L);
        user.setName("John Farmer");
        user.setEmail("farmer@example.com");

        when(mockRepository.findByCropCropId(999L)).thenReturn(List.of());

        ReportController controller = new ReportController(pdfReportService, mockRepository);

        ResponseEntity<byte[]> response = controller.generatePdfFromCrop(999L, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private PlantingInformation buildPlanting(Long plantingId, User user, Crop crop, String locationName, double area) {
        PlantingInformation planting = new PlantingInformation();
        planting.setPlantingId(plantingId);
        planting.setUser(user);
        planting.setCrop(crop);
        planting.setLocationName(locationName);
        planting.setArea(area);
        planting.setPlantingDate(LocalDate.now().minusMonths(3));
        planting.setExpectedHarvestDate(LocalDate.now().plusMonths(2));
        planting.setCurrentStatus(CurrentStatus.HEALTHY);
        planting.setIrrigationType(IrrigationType.DRIP);

        SoilType soilType = new SoilType();
        soilType.setSoilId(1L);
        soilType.setName("Loamy");
        planting.setSoilType(soilType);

        return planting;
    }
}
