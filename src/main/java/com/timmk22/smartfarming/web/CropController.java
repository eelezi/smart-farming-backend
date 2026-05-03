package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.response.IdNameResponse;
import com.timmk22.smartfarming.dto.response.PlantDiseaseDiagnosisResponse;
import com.timmk22.smartfarming.service.CropService;
import com.timmk22.smartfarming.service.PlantDiseaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;
    private final PlantDiseaseService plantDiseaseService;

    public CropController(CropService cropService, PlantDiseaseService plantDiseaseService) {
        this.cropService = cropService;
        this.plantDiseaseService = plantDiseaseService;
    }

    @GetMapping
    public ResponseEntity<List<IdNameResponse>> getAll() {
        return ResponseEntity.ok(cropService.listAll());
    }

    @PostMapping(value = "/diagnosis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> diagnosePlant(
            @RequestPart("image") MultipartFile image) {
        try {
            PlantDiseaseDiagnosisResponse response = plantDiseaseService.analyzeImage(image);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }
}
