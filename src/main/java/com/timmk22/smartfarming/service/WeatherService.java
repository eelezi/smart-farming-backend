package com.timmk22.smartfarming.service;

import java.math.BigDecimal;
import java.util.List;

import com.timmk22.smartfarming.dto.response.WeatherResponse;

public interface WeatherService {
    
    /**
     * Fetch weather forecast from Open-Meteo for the given coordinates,
     * persist it to the database, and return the result.
     *
     * @param latitude  latitude in decimal degrees (-90 to 90)
     * @param longitude longitude in decimal degrees (-180 to 180)
     * @return the saved forecast as a WeatherResponse
     * @throws IllegalArgumentException if coordinates are out of range
     * @throws RuntimeException         if the Open-Meteo API call fails
     */
    List<WeatherResponse> getWeather(BigDecimal latitude, BigDecimal longitude, String timezone);
}
