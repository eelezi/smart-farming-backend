package com.timmk22.smartfarming.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "soil_type")
@Getter
@Setter
@NoArgsConstructor
public class SoilType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "soil_id")
    private Long soilId;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80, unique = true)
    private String name;

    @OneToMany(mappedBy = "soilType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlantingInformation> plantings = new ArrayList<>();

}

