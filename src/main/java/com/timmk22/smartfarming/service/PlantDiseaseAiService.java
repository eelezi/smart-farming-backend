package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.response.PlantDiseaseDiagnosisResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PlantDiseaseAiService {

    PlantDiseaseDiagnosisResponse analyzeImage(MultipartFile image);
}

