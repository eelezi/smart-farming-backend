package com.timmk22.smartfarming.dto.request;

import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.enumeration.IrrigationType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlantingInformationRequest {

    @NotNull(message = "Area is required")
    @Positive(message = "Area must be positive")
    private Double area;

    @NotNull(message = "Planting date is required")
    private LocalDate plantingDate;

    @NotNull(message = "Crop ID is required")
    @Positive(message = "Crop ID must be positive")
    private Long cropId;

    @NotNull(message = "Soil type ID is required")
    @Positive(message = "Soil type ID must be positive")
    private Long soilTypeId;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @Size(max = 255, message = "Location name must not exceed 255 characters")
    private String locationName;

    private IrrigationType irrigationType;

    private CurrentStatus currentStatus;

    private LocalDate expectedHarvestDate;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
