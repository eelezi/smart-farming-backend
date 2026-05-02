package com.timmk22.smartfarming.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hourly_forecast")
@Getter
@Setter
@NoArgsConstructor
public class HourlyForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "forecast_id", nullable = false)
    private Forecast forecast;

    private LocalDateTime time;

    @Column(name = "temp_2m", precision = 5, scale = 2)
    private BigDecimal temp2m;

    @Column(name = "relat_hum_2m", precision = 5, scale = 2)
    private BigDecimal relatHum2m;

    @Column(name = "cloud_cover", precision = 5, scale = 2)
    private BigDecimal cloudCover;

    @Column(name = "wind_speed_10m", precision = 6, scale = 2)
    private BigDecimal windSpeed10m;

    @Column(name = "soil_moisture_9_to_27cm", precision = 6, scale = 2)
    private BigDecimal soilMoisture9To27cm;

    @Column(name = "direct_norm_irradiance", precision = 8, scale = 2)
    private BigDecimal directNormIrradiance;

    @Column(name = "vapour_pressure_deficit", precision = 6, scale = 2)
    private BigDecimal vapourPressureDeficit;

    @Column(name = "evapotranspiration", precision = 6, scale = 2)
    private BigDecimal evapotranspiration;
}

