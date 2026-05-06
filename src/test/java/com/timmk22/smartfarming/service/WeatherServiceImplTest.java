package com.timmk22.smartfarming.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmk22.smartfarming.dto.response.WeatherResponse;
import com.timmk22.smartfarming.model.Forecast;
import com.timmk22.smartfarming.model.Recommendation;
import com.timmk22.smartfarming.repository.ForecastRepository;
import com.timmk22.smartfarming.repository.RecommendationRepository;
import com.timmk22.smartfarming.service.impl.WeatherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WeatherServiceImplTest {

    private ForecastRepository forecastRepository;
    private RecommendationRepository recommendationRepository;
    private ObjectMapper objectMapper;
    private RestTemplate restTemplate;

    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setUp() {
        forecastRepository = mock(ForecastRepository.class);
        recommendationRepository = mock(RecommendationRepository.class);
        objectMapper = new ObjectMapper();
        restTemplate = mock(RestTemplate.class);

        weatherService = new WeatherServiceImpl(
                forecastRepository,
                recommendationRepository,
                objectMapper,
                restTemplate
        );
    }

    private Recommendation createRecommendation(Long id) {
        Recommendation recommendation = new Recommendation();
        recommendation.setRecommendationId(id);
        recommendation.setForecasts(new ArrayList<>());
        return recommendation;
    }

    private String validOpenMeteoResponse() {
        return """
                {
                  "daily": {
                    "time": ["2026-05-06"],
                    "temperature_2m_max": [28.5],
                    "temperature_2m_min": [14.2],
                    "sunrise": ["2026-05-06T05:10"],
                    "sunset": ["2026-05-06T19:30"],
                    "precipitation_probability_max": [60.0],
                    "rain_sum": [12.3],
                    "showers_sum": [3.4],
                    "snowfall_sum": [0.0]
                  },
                  "hourly": {
                    "time": [
                      "2026-05-06T00:00","2026-05-06T01:00","2026-05-06T02:00","2026-05-06T03:00",
                      "2026-05-06T04:00","2026-05-06T05:00","2026-05-06T06:00","2026-05-06T07:00",
                      "2026-05-06T08:00","2026-05-06T09:00","2026-05-06T10:00","2026-05-06T11:00",
                      "2026-05-06T12:00","2026-05-06T13:00","2026-05-06T14:00","2026-05-06T15:00",
                      "2026-05-06T16:00","2026-05-06T17:00","2026-05-06T18:00","2026-05-06T19:00",
                      "2026-05-06T20:00","2026-05-06T21:00","2026-05-06T22:00","2026-05-06T23:00"
                    ],
                    "temperature_2m": [
                      15.0,15.1,15.2,15.3,15.4,15.5,15.6,15.7,15.8,15.9,16.0,16.1,
                      16.2,16.3,16.4,16.5,16.6,16.7,16.8,16.9,17.0,17.1,17.2,17.3
                    ],
                    "relative_humidity_2m": [
                      70,70,69,69,68,68,67,67,66,66,65,65,
                      64,64,63,63,62,62,61,61,60,60,59,59
                    ],
                    "cloud_cover": [
                      10,10,11,11,12,12,13,13,14,14,15,15,
                      16,16,17,17,18,18,19,19,20,20,21,21
                    ],
                    "wind_speed_10m": [
                      5,5,5,5,6,6,6,6,7,7,7,7,
                      8,8,8,8,9,9,9,9,10,10,10,10
                    ],
                    "soil_moisture_9_to_27cm": [
                      20,20,20,20,21,21,21,21,22,22,22,22,
                      23,23,23,23,24,24,24,24,25,25,25,25
                    ],
                    "direct_normal_irradiance": [
                      100,100,110,110,120,120,130,130,140,140,150,150,
                      160,160,170,170,180,180,190,190,200,200,210,210
                    ],
                    "vapour_pressure_deficit": [
                      1.0,1.0,1.1,1.1,1.2,1.2,1.3,1.3,1.4,1.4,1.5,1.5,
                      1.6,1.6,1.7,1.7,1.8,1.8,1.9,1.9,2.0,2.0,2.1,2.1
                    ],
                    "et0_fao_evapotranspiration": [
                      0.1,0.1,0.1,0.1,0.2,0.2,0.2,0.2,0.3,0.3,0.3,0.3,
                      0.4,0.4,0.4,0.4,0.5,0.5,0.5,0.5,0.6,0.6,0.6,0.6
                    ]
                  }
                }
                """;
    }

    @Test
    @DisplayName("Should get weather successfully and save forecasts")
    void shouldGetWeatherSuccessfullyAndSaveForecasts() {
        Recommendation recommendation = createRecommendation(1L);

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(validOpenMeteoResponse());
        when(forecastRepository.save(any(Forecast.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(recommendationRepository.save(any(Recommendation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<WeatherResponse> result = weatherService.getWeather(
                BigDecimal.valueOf(41.63),
                BigDecimal.valueOf(22.47),
                "UTC",
                1L
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLatitude()).isEqualByComparingTo("41.63");
        assertThat(result.get(0).getLongitude()).isEqualByComparingTo("22.47");
        assertThat(result.get(0).getTimezone()).isEqualTo("UTC");
        assertThat(result.get(0).getForecastDays()).isEqualTo(1);
        assertThat(result.get(0).getTemp2mMax()).isEqualByComparingTo("28.5");
        assertThat(result.get(0).getTemp2mMin()).isEqualByComparingTo("14.2");
        assertThat(result.get(0).getHourlyForecasts()).hasSize(24);

        verify(recommendationRepository).findById(1L);
        verify(restTemplate).getForObject(contains("latitude=41.63"), eq(String.class));
        verify(forecastRepository).save(any(Forecast.class));
        verify(recommendationRepository).save(recommendation);
    }

    @Test
    @DisplayName("Should throw when recommendation is not found")
    void shouldThrowWhenRecommendationIsNotFound() {
        when(recommendationRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                weatherService.getWeather(
                        BigDecimal.valueOf(41.63),
                        BigDecimal.valueOf(22.47),
                        "UTC",
                        99L
                )
        );

        assertThat(ex.getMessage()).isEqualTo("Recommendation not found for id: 99");
        verify(recommendationRepository).findById(99L);
        verifyNoInteractions(restTemplate, forecastRepository);
    }

    @Test
    @DisplayName("Should throw when latitude is below minimum")
    void shouldThrowWhenLatitudeIsBelowMinimum() {
        Recommendation recommendation = createRecommendation(1L);
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                weatherService.getWeather(
                        BigDecimal.valueOf(-91),
                        BigDecimal.valueOf(22.47),
                        "UTC",
                        1L
                )
        );

        assertThat(ex.getMessage()).contains("Latitude must be between -90 and 90");
        verifyNoInteractions(restTemplate, forecastRepository);
    }

    @Test
    @DisplayName("Should throw when longitude is above maximum")
    void shouldThrowWhenLongitudeIsAboveMaximum() {
        Recommendation recommendation = createRecommendation(1L);
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                weatherService.getWeather(
                        BigDecimal.valueOf(41.63),
                        BigDecimal.valueOf(181),
                        "UTC",
                        1L
                )
        );

        assertThat(ex.getMessage()).contains("Longitude must be between -180 and 180");
        verifyNoInteractions(restTemplate, forecastRepository);
    }

    @Test
    @DisplayName("Should throw when external API call fails")
    void shouldThrowWhenExternalApiCallFails() {
        Recommendation recommendation = createRecommendation(1L);

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                weatherService.getWeather(
                        BigDecimal.valueOf(41.63),
                        BigDecimal.valueOf(22.47),
                        "UTC",
                        1L
                )
        );

        assertThat(ex.getMessage()).contains("Failed to reach Open-Meteo API");
    }

    @Test
    @DisplayName("Should throw when Open Meteo returns error response")
    void shouldThrowWhenOpenMeteoReturnsErrorResponse() {
        Recommendation recommendation = createRecommendation(1L);

        String errorResponse = """
                {
                  "error": true,
                  "reason": "Invalid coordinates"
                }
                """;

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(errorResponse);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                weatherService.getWeather(
                        BigDecimal.valueOf(41.63),
                        BigDecimal.valueOf(22.47),
                        "UTC",
                        1L
                )
        );

        assertThat(ex.getMessage()).contains("Failed to parse Open-Meteo response");
    }

    @Test
    @DisplayName("Should connect forecast to recommendation before saving")
    void shouldConnectForecastToRecommendationBeforeSaving() {
        Recommendation recommendation = createRecommendation(1L);

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(validOpenMeteoResponse());
        when(forecastRepository.save(any(Forecast.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(recommendationRepository.save(any(Recommendation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        weatherService.getWeather(
                BigDecimal.valueOf(41.63),
                BigDecimal.valueOf(22.47),
                "UTC",
                1L
        );

        ArgumentCaptor<Forecast> captor = ArgumentCaptor.forClass(Forecast.class);
        verify(forecastRepository).save(captor.capture());

        Forecast savedForecast = captor.getValue();
        assertThat(savedForecast.getRecommendation()).isEqualTo(recommendation);
        assertThat(savedForecast.getHourlyForecasts()).hasSize(24);
        assertThat(savedForecast.getHourlyForecasts().get(0).getForecast()).isEqualTo(savedForecast);
    }
}