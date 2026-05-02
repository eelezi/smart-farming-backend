package com.timmk22.smartfarming.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmk22.smartfarming.dto.response.HourlyForecastResponse;
import com.timmk22.smartfarming.dto.response.WeatherResponse;
import com.timmk22.smartfarming.model.Forecast;
import com.timmk22.smartfarming.model.HourlyForecast;
import com.timmk22.smartfarming.model.Recommendation;
import com.timmk22.smartfarming.repository.ForecastRepository;
import com.timmk22.smartfarming.repository.RecommendationRepository;
import com.timmk22.smartfarming.service.WeatherService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final String OPEN_METEO_URL = "https://api.open-meteo.com/v1/forecast";

    private static final String DAILY_VARS =
            "temperature_2m_max,temperature_2m_min,sunrise,sunset," +
            "precipitation_probability_max,rain_sum,showers_sum,snowfall_sum";

    private static final String HOURLY_VARS =
            "temperature_2m,relative_humidity_2m,cloud_cover,wind_speed_10m," +
            "soil_moisture_9_to_27cm,direct_normal_irradiance," +
            "vapour_pressure_deficit,et0_fao_evapotranspiration";

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final ForecastRepository forecastRepository;
    private final RecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public WeatherServiceImpl(ForecastRepository forecastRepository,
                              RecommendationRepository recommendationRepository,
                              ObjectMapper objectMapper,
                              RestTemplate restTemplate) {
        this.forecastRepository = forecastRepository;
        this.recommendationRepository = recommendationRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<WeatherResponse> getWeather(BigDecimal latitude, BigDecimal longitude, String timezone, Long recommendation_id) {
        Recommendation recommendation = recommendationRepository.findById(recommendation_id)
                .orElseThrow(() -> new RuntimeException("Recommendation not found for id: " + recommendation_id));
        validateCoordinates(latitude, longitude);

        String responseBody = fetchFromOpenMeteo(latitude, longitude, timezone);

        List<Forecast> forecasts = parseAndBuildForecasts(responseBody, latitude, longitude, timezone);

        forecasts.forEach(forecast -> {
            forecast.setRecommendation(recommendation);
            Forecast saved = forecastRepository.save(forecast);
            recommendation.getForecasts().add(saved);
            recommendationRepository.save(recommendation);
        });

        return forecasts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (latitude.compareTo(BigDecimal.valueOf(-90)) < 0 ||
                latitude.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90, got: " + latitude);
        }
        if (longitude.compareTo(BigDecimal.valueOf(-180)) < 0 ||
                longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180, got: " + longitude);
        }
    }

    private String fetchFromOpenMeteo(BigDecimal latitude, BigDecimal longitude, String timezone) {
        try {
            String url = OPEN_METEO_URL +
                    "?latitude=" + latitude +
                    "&longitude=" + longitude +
                    "&daily=" + DAILY_VARS +
                    "&hourly=" + HOURLY_VARS +
                    "&forecast_days=7" +
                    "&timezone=" + timezone;

            return restTemplate.getForObject(url, String.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to reach Open-Meteo API: " + e.getMessage(), e);
        }
    }

    private List<Forecast> parseAndBuildForecasts(String responseBody,
                                                  BigDecimal latitude,
                                                  BigDecimal longitude,
                                                  String timezone) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            if (root.has("error") && root.get("error").asBoolean()) {
                String reason = root.path("reason").asText("Unknown error");
                throw new RuntimeException("Open-Meteo API error: " + reason);
            }

            JsonNode daily = root.path("daily");
            JsonNode hourly = root.path("hourly");

            int days = daily.path("time").size();
            int hours = hourly.path("time").size();

            List<Forecast> forecasts = new ArrayList<>();

            for (int i = 0; i < days; i++) {
                Forecast forecast = new Forecast();
                forecast.setLatitude(latitude);
                forecast.setLongitude(longitude);
                forecast.setTimezone(timezone);
                forecast.setForecastDays(days);

                // Daily values
                forecast.setTime(parseDateTime(daily, "time", i, true));
                forecast.setTemp2mMax(getDecimal(daily, "temperature_2m_max", i));
                forecast.setTemp2mMin(getDecimal(daily, "temperature_2m_min", i));
                forecast.setSunrise(parseDateTime(daily, "sunrise", i, true));
                forecast.setSunset(parseDateTime(daily, "sunset", i, true));
                forecast.setPercProbMax(getDecimal(daily, "precipitation_probability_max", i));
                forecast.setRainSum(getDecimal(daily, "rain_sum", i));
                forecast.setShowersSum(getDecimal(daily, "showers_sum", i));
                forecast.setSnowfallSum(getDecimal(daily, "snowfall_sum", i));

                // Hourly values for this day
                List<HourlyForecast> hourlyForecasts = new ArrayList<>();
                for (int h = i * 24; h < (i + 1) * 24 && h < hours; h++) {
                    HourlyForecast hf = new HourlyForecast();
                    hf.setForecast(forecast);
                    hf.setTime(parseDateTime(hourly, "time", h, false));
                    hf.setTemp2m(getDecimal(hourly, "temperature_2m", h));
                    hf.setRelatHum2m(getDecimal(hourly, "relative_humidity_2m", h));
                    hf.setCloudCover(getDecimal(hourly, "cloud_cover", h));
                    hf.setWindSpeed10m(getDecimal(hourly, "wind_speed_10m", h));
                    hf.setSoilMoisture9To27cm(getDecimal(hourly, "soil_moisture_9_to_27cm", h));
                    hf.setDirectNormIrradiance(getDecimal(hourly, "direct_normal_irradiance", h));
                    hf.setVapourPressureDeficit(getDecimal(hourly, "vapour_pressure_deficit", h));
                    hf.setEvapotranspiration(getDecimal(hourly, "et0_fao_evapotranspiration", h));
                    hourlyForecasts.add(hf);
                }

                forecast.setHourlyForecasts(hourlyForecasts);
                forecasts.add(forecast);
            }

            return forecasts;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Open-Meteo response: " + e.getMessage(), e);
        }
    }



    private BigDecimal getDecimal(JsonNode parent, String field, int index) {
        JsonNode array = parent.path(field);
        if (!array.isArray()) return null;
        JsonNode value = array.get(index);
        if (value == null || value.isNull()) return null;
        return new BigDecimal(value.asText());
    }

    private LocalDateTime parseDateTime(JsonNode parent, String field, int index, boolean dateOnly) {
        JsonNode array = parent.path(field);
        if (!array.isArray()) return null;
        JsonNode value = array.get(index);
        if (value == null || value.isNull()) return null;

        String raw = value.asText();

        if (dateOnly && !raw.contains("T")) {
            raw += "T00:00:00";
        } else if (!raw.contains("T")) {
            raw += ":00";
        }

        return LocalDateTime.parse(raw, ISO_FORMATTER);
    }

    private WeatherResponse convertToResponse(Forecast f) {
        WeatherResponse r = new WeatherResponse();
        r.setForecastId(f.getForecastId());
        r.setLatitude(f.getLatitude());
        r.setLongitude(f.getLongitude());
        r.setTimezone(f.getTimezone());
        r.setForecastDays(f.getForecastDays());
        r.setTime(f.getTime());
        r.setTemp2mMax(f.getTemp2mMax());
        r.setTemp2mMin(f.getTemp2mMin());
        r.setSunrise(f.getSunrise());
        r.setSunset(f.getSunset());
        r.setPercProbMax(f.getPercProbMax());
        r.setRainSum(f.getRainSum());
        r.setShowersSum(f.getShowersSum());
        r.setSnowfallSum(f.getSnowfallSum());

        List<HourlyForecastResponse> hourlyResponses = f.getHourlyForecasts().stream()
                .map(hf -> new HourlyForecastResponse(
                        hf.getTime(),
                        hf.getTemp2m(),
                        hf.getRelatHum2m(),
                        hf.getCloudCover(),
                        hf.getWindSpeed10m(),
                        hf.getSoilMoisture9To27cm(),
                        hf.getDirectNormIrradiance(),
                        hf.getVapourPressureDeficit(),
                        hf.getEvapotranspiration()
                ))
                .collect(Collectors.toList());

        r.setHourlyForecasts(hourlyResponses);
        return r;
    }

}