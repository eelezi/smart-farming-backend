package com.timmk22.smartfarming.model;

import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.enumeration.IrrigationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planting_information")
@Getter
@Setter
@NoArgsConstructor
public class PlantingInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planting_id")
    private Long plantingId;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double area;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Column(name = "latitude")
    private Double latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Column(name = "longitude")
    private Double longitude;

    @Size(max = 255)
    @Column(name = "location_name")
    private String locationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "irrigation_type", length = 50)
    private IrrigationType irrigationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 50)
    private CurrentStatus currentStatus = CurrentStatus.HEALTHY;

    @Column(name = "expected_harvest_date")
    private LocalDate expectedHarvestDate;

    @NotNull
    @Column(name = "planting_date", nullable = false)
    private LocalDate plantingDate;

    @Size(max = 1000)
    @Column(name = "notes", length = 1000)
    private String notes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "soil_id", nullable = false)
    private SoilType soilType;

    @OneToMany(mappedBy = "plantingInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recommendation> recommendations = new ArrayList<>();

}

