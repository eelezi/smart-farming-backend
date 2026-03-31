package com.timmk22.smartfarming.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlantingInformationResponse {

    private Long plantingId;
    private Double area;
    private LocalDate plantingDate;
    private Long userId;
    private Long cropId;
    private String cropName;
    private Long soilTypeId;
    private String soilTypeName;
}
