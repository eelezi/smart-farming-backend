package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.response.LocationDTO;
import com.timmk22.smartfarming.dto.response.WeatherResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final com.timmk22.smartfarming.service.WeatherService weatherService;
    private final RestTemplate restTemplate;

    public WeatherController(com.timmk22.smartfarming.service.WeatherService weatherService,
                             RestTemplate restTemplate) {
        this.weatherService = weatherService;
        this.restTemplate = restTemplate;
    }

    /**
     * Fetch and save a weather forecast for the given coordinates.
     * GET /weather?latitude=41.9981&longitude=21.4254
     */
    @GetMapping
    public ResponseEntity<?> getWeather(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(required = false) String timezone) {
        try {
            if (timezone == null || timezone.isBlank()) {
                timezone = "UTC";
            }
            List<WeatherResponse> response = weatherService.getWeather(latitude, longitude, timezone);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }

    /**
     * Convert a city/village name into latitude and longitude using OpenStreetMap Nominatim.
     * GET /weather/geocode?q=dracevo
     */
    @GetMapping("/geocode")
public ResponseEntity<?> geocode(@RequestParam String q) { // matches ?q=dracevo
    try {
        String url = "https://nominatim.openstreetmap.org/search?q=" + q + "&format=json&limit=10";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "SmartFarmingApp/1.0 (dimitarbalo@gmail.com)"); 

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<LocationDTO[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, LocationDTO[].class
        );

        return ResponseEntity.ok(response.getBody());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Failed to fetch location data: " + e.getMessage());
    }
}

    
}