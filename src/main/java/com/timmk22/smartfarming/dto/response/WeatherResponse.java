package com.timmk22.smartfarming.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private Long forecastId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String timezone;
    private Integer forecastDays;
    private LocalDateTime time;
    private BigDecimal temp2mMax;
    private BigDecimal temp2mMin;
    private LocalDateTime sunrise;
    private LocalDateTime sunset;
    private BigDecimal percProbMax;
    private BigDecimal rainSum;
    private BigDecimal showersSum;
    private BigDecimal snowfallSum;

    private List<HourlyForecastResponse> hourlyForecasts;
}
