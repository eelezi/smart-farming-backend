package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.response.PlantDiseaseDiagnosisResponse;
import com.timmk22.smartfarming.service.PlantDiseaseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/diseases")
@RequiredArgsConstructor
public class PlantDiseaseController {

    private final PlantDiseaseService plantDiseaseService;

    @PostMapping(value = "/predict", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<@NonNull PlantDiseaseDiagnosisResponse> predictDisease(
            @RequestPart(value = "image", required = true) MultipartFile image) {
        return ResponseEntity.ok(plantDiseaseService.analyzeImage(image));
    }
}

