package com.timmk22.smartfarming.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmk22.smartfarming.dto.response.WeatherResponse;
import com.timmk22.smartfarming.model.Forecast;
import com.timmk22.smartfarming.model.Recommendation;
import com.timmk22.smartfarming.repository.ForecastRepository;
import com.timmk22.smartfarming.repository.RecommendationRepository;
import com.timmk22.smartfarming.service.WeatherService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public WeatherResponse getWeather(BigDecimal latitude, BigDecimal longitude, String timezone) {
        validateCoordinates(latitude, longitude);

        String responseBody = fetchFromOpenMeteo(latitude, longitude, timezone);

        Forecast forecast = parseAndBuildForecast(responseBody, latitude, longitude, timezone);

        // --- Commented for now to avoid null recom_id errors
        /*
        Recommendation recommendation = recommendationRepository
                .findAll()
                .stream()
                .findFirst()
                .orElse(null);

        if (recommendation != null) {
            forecast.setRecommendation(recommendation);
            Forecast saved = forecastRepository.save(forecast);
            return convertToResponse(saved);
        }
        */

        //Forecast saved = forecastRepository.save(forecast);
        return convertToResponse(forecast);
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

    private Forecast parseAndBuildForecast(String responseBody,
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

            Forecast forecast = new Forecast();
            forecast.setLatitude(latitude);
            forecast.setLongitude(longitude);

            forecast.setTimezone(timezone);
            forecast.setForecastDays(7);

            forecast.setTime(parseDateTime(daily, "time", 0, true));
            forecast.setTemp2mMax(getDecimal(daily, "temperature_2m_max", 0));
            forecast.setTemp2mMin(getDecimal(daily, "temperature_2m_min", 0));
            forecast.setSunrise(parseDateTime(daily, "sunrise", 0, true));
            forecast.setSunset(parseDateTime(daily, "sunset", 0, true));
            forecast.setPercProbMax(getDecimal(daily, "precipitation_probability_max", 0));
            forecast.setRainSum(getDecimal(daily, "rain_sum", 0));
            forecast.setShowersSum(getDecimal(daily, "showers_sum", 0));
            forecast.setSnowfallSum(getDecimal(daily, "snowfall_sum", 0));

            forecast.setHourlyTime(parseDateTime(hourly, "time", 0, false));
            forecast.setTemp2m(getDecimal(hourly, "temperature_2m", 0));
            forecast.setRelatHum2m(getDecimal(hourly, "relative_humidity_2m", 0));
            forecast.setCloudCover(getDecimal(hourly, "cloud_cover", 0));
            forecast.setWindSpeed10m(getDecimal(hourly, "wind_speed_10m", 0));
            forecast.setSoilMoisture9To27cm(getDecimal(hourly, "soil_moisture_9_to_27cm", 0));
            forecast.setDirectNormIrradiance(getDecimal(hourly, "direct_normal_irradiance", 0));
            forecast.setVapourPressureDeficit(getDecimal(hourly, "vapour_pressure_deficit", 0));
            forecast.setEvapotranspiration(getDecimal(hourly, "et0_fao_evapotranspiration", 0));

            return forecast;

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
        r.setHourlyTime(f.getHourlyTime());
        r.setTemp2m(f.getTemp2m());
        r.setRelatHum2m(f.getRelatHum2m());
        r.setCloudCover(f.getCloudCover());
        r.setWindSpeed10m(f.getWindSpeed10m());
        r.setSoilMoisture9To27cm(f.getSoilMoisture9To27cm());
        r.setDirectNormIrradiance(f.getDirectNormIrradiance());
        r.setVapourPressureDeficit(f.getVapourPressureDeficit());
        r.setEvapotranspiration(f.getEvapotranspiration());

        return r;
    }
}