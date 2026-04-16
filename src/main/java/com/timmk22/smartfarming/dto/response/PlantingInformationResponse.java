package com.timmk22.smartfarming.dto.response;

import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.enumeration.IrrigationType;
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
    private Double latitude;
    private Double longitude;
    private String locationName;
    private IrrigationType irrigationType;
    private CurrentStatus currentStatus;
    private LocalDate expectedHarvestDate;
    private String notes;

}
