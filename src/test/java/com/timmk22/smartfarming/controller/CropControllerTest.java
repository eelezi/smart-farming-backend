package com.timmk22.smartfarming.controller;

import com.timmk22.smartfarming.dto.response.IdNameResponse;
import com.timmk22.smartfarming.dto.response.PlantDiseaseDiagnosisResponse;
import com.timmk22.smartfarming.enumeration.PlantDiseaseDetectionStatus;
import com.timmk22.smartfarming.service.CropService;
import com.timmk22.smartfarming.service.PlantDiseaseService;
import com.timmk22.smartfarming.web.CropController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CropControllerTest {

    @Test
    void getAllShouldReturnAllCrops() {
        CropService cropService = mock(CropService.class);
        PlantDiseaseService plantDiseaseService = mock(PlantDiseaseService.class);

        List<IdNameResponse> crops = List.of(
                new IdNameResponse(1L, "Wheat"),
                new IdNameResponse(2L, "Tomato")
        );

        when(cropService.listAll()).thenReturn(crops);

        CropController controller = new CropController(cropService, plantDiseaseService);

        ResponseEntity<List<IdNameResponse>> response = controller.getAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).name()).isEqualTo("Wheat");
        assertThat(response.getBody().get(1).name()).isEqualTo("Tomato");

        verify(cropService, times(1)).listAll();
        verifyNoInteractions(plantDiseaseService);
    }

    @Test
    void diagnosePlantShouldReturnDiagnosisWhenImageIsValid() {
        CropService cropService = mock(CropService.class);
        PlantDiseaseService plantDiseaseService = mock(PlantDiseaseService.class);

        MultipartFile image = mock(MultipartFile.class);

        PlantDiseaseDiagnosisResponse diagnosisResponse = new PlantDiseaseDiagnosisResponse(
                PlantDiseaseDetectionStatus.DISEASE_FOUND,
                "Tomato Early Blight - Fungal disease detected on leaves. Remove infected leaves and apply fungicide."
        );

        when(plantDiseaseService.analyzeImage(image)).thenReturn(diagnosisResponse);

        CropController controller = new CropController(cropService, plantDiseaseService);

        ResponseEntity<?> response = controller.diagnosePlant(image);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(PlantDiseaseDiagnosisResponse.class);

        PlantDiseaseDiagnosisResponse body = (PlantDiseaseDiagnosisResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(PlantDiseaseDetectionStatus.DISEASE_FOUND);
        assertThat(body.getAnalysis()).contains("Tomato Early Blight");

        verify(plantDiseaseService, times(1)).analyzeImage(image);
        verifyNoInteractions(cropService);
    }

    @Test
    void diagnosePlantShouldReturnBadRequestWhenImageIsInvalid() {
        CropService cropService = mock(CropService.class);
        PlantDiseaseService plantDiseaseService = mock(PlantDiseaseService.class);

        MultipartFile image = mock(MultipartFile.class);

        when(plantDiseaseService.analyzeImage(image))
                .thenThrow(new IllegalArgumentException("Invalid image file."));

        CropController controller = new CropController(cropService, plantDiseaseService);

        ResponseEntity<?> response = controller.diagnosePlant(image);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid image file.");

        verify(plantDiseaseService, times(1)).analyzeImage(image);
        verifyNoInteractions(cropService);
    }

    @Test
    void diagnosePlantShouldReturnServiceUnavailableWhenServiceFails() {
        CropService cropService = mock(CropService.class);
        PlantDiseaseService plantDiseaseService = mock(PlantDiseaseService.class);

        MultipartFile image = mock(MultipartFile.class);

        when(plantDiseaseService.analyzeImage(image))
                .thenThrow(new RuntimeException("AI service is temporarily unavailable."));

        CropController controller = new CropController(cropService, plantDiseaseService);

        ResponseEntity<?> response = controller.diagnosePlant(image);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isEqualTo("AI service is temporarily unavailable.");

        verify(plantDiseaseService, times(1)).analyzeImage(image);
        verifyNoInteractions(cropService);
    }
}