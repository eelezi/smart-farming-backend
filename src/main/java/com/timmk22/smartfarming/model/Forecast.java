package com.timmk22.smartfarming.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "rain_sum", precision = 6, scale = 2)
    private BigDecimal rainSum;

    @Column(name = "showers_sum", precision = 6, scale = 2)
    private BigDecimal showersSum;

    @Column(name = "snowfall_sum", precision = 6, scale = 2)
    private BigDecimal snowfallSum;

    @OneToMany(mappedBy = "forecast", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HourlyForecast> hourlyForecasts = new ArrayList<>();
}
