package com.timmk22.smartfarming.dto.response;

import com.timmk22.smartfarming.enumeration.PlantDiseaseDetectionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlantDiseaseDiagnosisResponse {

    private PlantDiseaseDetectionStatus status;
    private String analysis;
}

