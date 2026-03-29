package com.timmk22.smartfarming.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "forecast")
@Getter
@Setter
@NoArgsConstructor
public class Forecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forecast_id")
    private Long forecastId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recom_id", nullable = false)
    private Recommendation recommendation;

    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(length = 100)
    private String timezone;

    @Column(name = "forecast_days")
    private Integer forecastDays;

    private LocalDateTime time;

    @Column(name = "temp_2m_max", precision = 5, scale = 2)
    private BigDecimal temp2mMax;

    @Column(name = "temp_2m_min", precision = 5, scale = 2)
    private BigDecimal temp2mMin;

    private LocalDateTime sunrise;

    private LocalDateTime sunset;

    @Column(name = "perc_prob_max", precision = 5, scale = 2)
    private BigDecimal percProbMax;

    @Column(name = "soil_moisture_9_to_27cm", precision = 6, scale = 2)
    private BigDecimal soilMoisture9To27cm;

    @Column(name = "cloud_cover", precision = 5, scale = 2)
    private BigDecimal cloudCover;

    @Column(name = "direct_norm_irradiance", precision = 8, scale = 2)
    private BigDecimal directNormIrradiance;

    @Column(name = "wind_speed_10m", precision = 6, scale = 2)
    private BigDecimal windSpeed10m;

    @Column(name = "vapour_pressure_deficit", precision = 6, scale = 2)
    private BigDecimal vapourPressureDeficit;

    @Column(name = "evapotranspiration", precision = 6, scale = 2)
    private BigDecimal evapotranspiration;

    @Column(name = "relat_hum_2m", precision = 5, scale = 2)
    private BigDecimal relatHum2m;

    @Column(name = "temp_2m", precision = 5, scale = 2)
    private BigDecimal temp2m;

    @Column(name = "hourly_time")
    private LocalDateTime hourlyTime;

    @Column(name = "rain_sum", precision = 6, scale = 2)
    private BigDecimal rainSum;

    @Column(name = "showers_sum", precision = 6, scale = 2)
    private BigDecimal showersSum;

    @Column(name = "snowfall_sum", precision = 6, scale = 2)
    private BigDecimal snowfallSum;

}

