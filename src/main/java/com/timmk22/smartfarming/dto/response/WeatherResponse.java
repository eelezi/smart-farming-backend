package com.timmk22.smartfarming.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {

    private Long forecastId;

    // Location
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String timezone;
    private Integer forecastDays;

    // Daily fields
    private LocalDateTime time;
    private BigDecimal temp2mMax;
    private BigDecimal temp2mMin;
    private LocalDateTime sunrise;
    private LocalDateTime sunset;
    private BigDecimal percProbMax;
    private BigDecimal rainSum;
    private BigDecimal showersSum;
    private BigDecimal snowfallSum;

    // Hourly fields
    private LocalDateTime hourlyTime;
    private BigDecimal temp2m;
    private BigDecimal relatHum2m;
    private BigDecimal cloudCover;
    private BigDecimal windSpeed10m;
    private BigDecimal soilMoisture9To27cm;
    private BigDecimal directNormIrradiance;
    private BigDecimal vapourPressureDeficit;
    private BigDecimal evapotranspiration;
}