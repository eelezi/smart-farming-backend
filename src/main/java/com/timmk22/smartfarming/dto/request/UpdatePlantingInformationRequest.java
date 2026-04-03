package com.timmk22.smartfarming.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlantingInformationRequest {

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
}
