package com.timmk22.smartfarming.controller;

import com.timmk22.smartfarming.dto.response.LocationDTO;
import com.timmk22.smartfarming.dto.response.WeatherResponse;
import com.timmk22.smartfarming.service.WeatherService;
import com.timmk22.smartfarming.web.WeatherController;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WeatherControllerTest {

    @Test
    void getWeatherShouldReturnForecastWhenRequestIsValid() {
        WeatherService weatherService = mock(WeatherService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        WeatherController controller = new WeatherController(weatherService, restTemplate);

        BigDecimal latitude = new BigDecimal("41.9981");
        BigDecimal longitude = new BigDecimal("21.4254");
        Long recommendationId = 1L;

        List<WeatherResponse> forecast = List.of(
                mock(WeatherResponse.class),
                mock(WeatherResponse.class)
        );

        when(weatherService.getWeather(latitude, longitude, "Europe/Skopje", recommendationId))
                .thenReturn(forecast);

        ResponseEntity<?> response = controller.getWeather(
                latitude,
                longitude,
                "Europe/Skopje",
                recommendationId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(forecast);

        verify(weatherService, times(1))
                .getWeather(latitude, longitude, "Europe/Skopje", recommendationId);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getWeatherShouldUseUtcWhenTimezoneIsNull() {
        WeatherService weatherService = mock(WeatherService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        WeatherController controller = new WeatherController(weatherService, restTemplate);

        BigDecimal latitude = new BigDecimal("41.9981");
        BigDecimal longitude = new BigDecimal("21.4254");
        Long recommendationId = 1L;

        List<WeatherResponse> forecast = List.of(mock(WeatherResponse.class));

        when(weatherService.getWeather(latitude, longitude, "UTC", recommendationId))
                .thenReturn(forecast);

        ResponseEntity<?> response = controller.getWeather(
                latitude,
                longitude,
                null,
                recommendationId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(forecast);

        verify(weatherService, times(1))
                .getWeather(latitude, longitude, "UTC", recommendationId);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getWeatherShouldUseUtcWhenTimezoneIsBlank() {
        WeatherService weatherService = mock(WeatherService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        WeatherController controller = new WeatherController(weatherService, restTemplate);

        BigDecimal latitude = new BigDecimal("41.9981");
        BigDecimal longitude = new BigDecimal("21.4254");
        Long recommendationId = 1L;

        List<WeatherResponse> forecast = List.of(mock(WeatherResponse.class));

        when(weatherService.getWeather(latitude, longitude, "UTC", recommendationId))
                .thenReturn(forecast);

        ResponseEntity<?> response = controller.getWeather(
                latitude,
                longitude,
                "   ",
                recommendationId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(forecast);

        verify(weatherService, times(1))
                .getWeather(latitude, longitude, "UTC", recommendationId);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getWeatherShouldReturnBadRequestWhenServiceThrowsIllegalArgumentException() {
        WeatherService weatherService = mock(WeatherService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        WeatherController controller = new WeatherController(weatherService, restTemplate);

        BigDecimal latitude = new BigDecimal("41.9981");
        BigDecimal longitude = new BigDecimal("21.4254");
        Long recommendationId = 1L;

        when(weatherService.getWeather(latitude, longitude, "UTC", recommendationId))
                .thenThrow(new IllegalArgumentException("Invalid coordinates"));

        ResponseEntity<?> response = controller.getWeather(
                latitude,
                longitude,
                null,
                recommendationId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid coordinates");

        verify(weatherService, times(1))
                .getWeather(latitude, longitude, "UTC", recommendationId);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getWeatherShouldReturnServiceUnavailableWhenServiceThrowsRuntimeException() {
        WeatherService weatherService = mock(WeatherService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        WeatherController controller = new WeatherController(weatherService, restTemplate);

        BigDecimal latitude = new BigDecimal("41.9981");
        BigDecimal longitude = new BigDecimal("21.4254");
        Long recommendationId = 1L;

        when(weatherService.getWeather(latitude, longitude, "UTC", recommendationId))
                .thenThrow(new RuntimeException("Weather service unavailable"));

        ResponseEntity<?> response = controller.getWeather(
                latitude,
                longitude,
                null,
                recommendationId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isEqualTo("Weather service unavailable");

        verify(weatherService, times(1))
                .getWeather(latitude, longitude, "UTC", recommendationId);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void geocodeShouldReturnLocationsWhenRequestSucceeds() {
        WeatherService weatherService = mock(WeatherService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        WeatherController controller = new WeatherController(weatherService, restTemplate);

        LocationDTO first = mock(LocationDTO.class);
        LocationDTO second = mock(LocationDTO.class);
        LocationDTO[] locations = new LocationDTO[]{first, second};

        ResponseEntity<LocationDTO[]> externalResponse = new ResponseEntity<>(locations, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(LocationDTO[].class)
        )).thenReturn(externalResponse);

        ResponseEntity<?> response = controller.geocode("dracevo");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(locations);

        verify(restTemplate, times(1)).exchange(
                contains("q=dracevo"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(LocationDTO[].class)
        );
        verifyNoInteractions(weatherService);
    }

    @Test
    void geocodeShouldReturnServiceUnavailableWhenRestTemplateFails() {
        WeatherService weatherService = mock(WeatherService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        WeatherController controller = new WeatherController(weatherService, restTemplate);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(LocationDTO[].class)
        )).thenThrow(new RuntimeException("Nominatim unavailable"));

        ResponseEntity<?> response = controller.geocode("dracevo");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isEqualTo("Failed to fetch location data: Nominatim unavailable");

        verify(restTemplate, times(1)).exchange(
                contains("q=dracevo"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(LocationDTO[].class)
        );
        verifyNoInteractions(weatherService);
    }
}