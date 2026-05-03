package com.timmk22.smartfarming.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.Schema;
import com.timmk22.smartfarming.dto.response.RecommendationResponse;
import com.timmk22.smartfarming.dto.response.WeatherResponse;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.model.Recommendation;
import com.timmk22.smartfarming.repository.PlantingInformationRepository;
import com.timmk22.smartfarming.repository.RecommendationRepository;
import com.timmk22.smartfarming.service.RecommendationService;
import com.timmk22.smartfarming.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final PlantingInformationRepository plantingInformationRepository;
    private final WeatherService weatherService;
    private final RecommendationRepository recommendationRepository;
    private final GoogleGenAiChatModel chatModel;
    private final Schema recommendationSchema;
    private final ObjectMapper objectMapper;

    @Value("${app.ai.recommendation-system-prompt}")
    private String recommendationPrompt;

    @Override
    @Transactional
    public RecommendationResponse generateRecommendation(Long plantingId, boolean summarized) {
        PlantingInformation plantingInfo = plantingInformationRepository.findById(plantingId)
                .orElseThrow(() -> new RuntimeException("PlantingInformation not found for id: " + plantingId));

        Recommendation recommendation = new Recommendation();
        recommendation.setCreatedAt(LocalDateTime.now());
        recommendation.setPlantingInformation(plantingInfo);
        recommendation.setRecommendationText("Temporary text");
        recommendationRepository.save(recommendation);

        List<WeatherResponse> weatherResponses = null;
        try {
            BigDecimal lat = BigDecimal.valueOf(plantingInfo.getLatitude());
            BigDecimal lon = BigDecimal.valueOf(plantingInfo.getLongitude());
            weatherResponses = weatherService.getWeather(lat, lon, "UTC", recommendation.getRecommendationId()); // TODO: Change timzeone to be dynamic based on user
        } catch (Exception e) {
            // Continue without weather data
        }

        SystemMessage systemMessage = new SystemMessage(recommendationPrompt);

        String contextString = buildContextString(plantingInfo, weatherResponses, summarized);

        Message userMessage = new UserMessage(contextString);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage),
                GoogleGenAiChatOptions.builder()
                        .responseMimeType("application/json")
                        .responseSchema(recommendationSchema.toJson())
                        .build());

        String responseText;
        try {
            String rawJson = chatModel.call(prompt).getResult().getOutput().getText();
            JsonNode rootNode = objectMapper.readTree(rawJson);
            responseText = rootNode.path("recommendationText").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AI recommendation: " + e.getMessage(), e);
        }

        recommendation.setRecommendationText(responseText);
        recommendation = recommendationRepository.save(recommendation);

        return new RecommendationResponse(
                recommendation.getRecommendationId(),
                recommendation.getRecommendationText(),
                recommendation.getCreatedAt(),
                plantingInfo.getPlantingId()
        );
    }

    private String buildContextString(PlantingInformation info, List<WeatherResponse> weatherResponses, boolean summarized) {
        StringBuilder sb = new StringBuilder();
        sb.append("Planting Information:\n");
        sb.append("Crop Name: ").append(info.getCrop().getName()).append("\n");
        sb.append("Irrigation Type: ").append(info.getIrrigationType() != null ? info.getIrrigationType().name() : "N/A").append("\n");
        sb.append("Current Status: ").append(info.getCurrentStatus() != null ? info.getCurrentStatus().name() : "N/A").append("\n");
        sb.append("Planting Date: ").append(info.getPlantingDate() != null ? info.getPlantingDate().toString() : "N/A").append("\n");

        sb.append("\nWeather Forecast Data:\n");
        if (weatherResponses != null && !weatherResponses.isEmpty()) {
            if (summarized) {
                appendWeatherSummary(sb, weatherResponses);
            } else {
                for (int i = 0; i < weatherResponses.size(); i++) {
                    WeatherResponse weather = weatherResponses.get(i);
                    sb.append("Day ").append(i + 1).append(":\n");
                    sb.append("Latitude: ").append(weather.getLatitude()).append("\n");
                    sb.append("Longitude: ").append(weather.getLongitude()).append("\n");
                    sb.append("Timezone: ").append(weather.getTimezone()).append("\n");
                    sb.append("Forecast Days: ").append(weather.getForecastDays()).append("\n");
                    sb.append("Time: ").append(weather.getTime()).append("\n");
                    sb.append("Max Temp 2m (C): ").append(weather.getTemp2mMax()).append("\n");
                    sb.append("Min Temp 2m (C): ").append(weather.getTemp2mMin()).append("\n");
                    sb.append("Sunrise: ").append(weather.getSunrise()).append("\n");
                    sb.append("Sunset: ").append(weather.getSunset()).append("\n");
                    sb.append("Precipitation Probability Max (%): ").append(weather.getPercProbMax()).append("\n");
                    sb.append("Rain Sum (mm): ").append(weather.getRainSum()).append("\n");
                    sb.append("Showers Sum (mm): ").append(weather.getShowersSum()).append("\n");
                    sb.append("Snowfall Sum: ").append(weather.getSnowfallSum()).append("\n");

                    if (i < weatherResponses.size() - 1) {
                        sb.append("\n");
                    }
                }
            }
        } else {
            sb.append("missing data\n");
        }

        return sb.toString();
    }

    private void appendWeatherSummary(StringBuilder sb, List<WeatherResponse> weatherResponses) {
        int days = weatherResponses.size();
        sb.append("Days Included: ").append(days).append("\n");

        BigDecimal maxTemp = null;
        BigDecimal minTemp = null;
        BigDecimal sumMaxTemp = BigDecimal.ZERO;
        BigDecimal sumMinTemp = BigDecimal.ZERO;
        int maxTempCount = 0;
        int minTempCount = 0;

        BigDecimal sumPercProb = BigDecimal.ZERO;
        int percProbCount = 0;

        BigDecimal rainTotal = BigDecimal.ZERO;
        BigDecimal showersTotal = BigDecimal.ZERO;
        BigDecimal snowfallTotal = BigDecimal.ZERO;

        LocalDateTime earliestSunrise = null;
        LocalDateTime latestSunset = null;

        for (WeatherResponse weather : weatherResponses) {
            BigDecimal dayMax = weather.getTemp2mMax();
            if (dayMax != null) {
                maxTemp = (maxTemp == null || dayMax.compareTo(maxTemp) > 0) ? dayMax : maxTemp;
                sumMaxTemp = sumMaxTemp.add(dayMax);
                maxTempCount++;
            }

            BigDecimal dayMin = weather.getTemp2mMin();
            if (dayMin != null) {
                minTemp = (minTemp == null || dayMin.compareTo(minTemp) < 0) ? dayMin : minTemp;
                sumMinTemp = sumMinTemp.add(dayMin);
                minTempCount++;
            }

            BigDecimal percProb = weather.getPercProbMax();
            if (percProb != null) {
                sumPercProb = sumPercProb.add(percProb);
                percProbCount++;
            }

            if (weather.getRainSum() != null) {
                rainTotal = rainTotal.add(weather.getRainSum());
            }
            if (weather.getShowersSum() != null) {
                showersTotal = showersTotal.add(weather.getShowersSum());
            }
            if (weather.getSnowfallSum() != null) {
                snowfallTotal = snowfallTotal.add(weather.getSnowfallSum());
            }

            LocalDateTime sunrise = weather.getSunrise();
            if (sunrise != null) {
                earliestSunrise = (earliestSunrise == null || sunrise.isBefore(earliestSunrise)) ? sunrise : earliestSunrise;
            }

            LocalDateTime sunset = weather.getSunset();
            if (sunset != null) {
                latestSunset = (latestSunset == null || sunset.isAfter(latestSunset)) ? sunset : latestSunset;
            }
        }

        sb.append("Overall Max Temp 2m (C): ").append(maxTemp).append("\n");
        sb.append("Overall Min Temp 2m (C): ").append(minTemp).append("\n");
        sb.append("Avg Max Temp 2m (C): ").append(average(sumMaxTemp, maxTempCount)).append("\n");
        sb.append("Avg Min Temp 2m (C): ").append(average(sumMinTemp, minTempCount)).append("\n");
        sb.append("Avg Precipitation Probability Max (%): ").append(average(sumPercProb, percProbCount)).append("\n");
        sb.append("Total Rain Sum (mm): ").append(rainTotal).append("\n");
        sb.append("Total Showers Sum (mm): ").append(showersTotal).append("\n");
        sb.append("Total Snowfall Sum: ").append(snowfallTotal).append("\n");
        sb.append("Earliest Sunrise: ").append(earliestSunrise).append("\n");
        sb.append("Latest Sunset: ").append(latestSunset).append("\n");

        WeatherResponse first = weatherResponses.get(0);
        sb.append("Latitude: ").append(first.getLatitude()).append("\n");
        sb.append("Longitude: ").append(first.getLongitude()).append("\n");
        sb.append("Timezone: ").append(first.getTimezone()).append("\n");
    }

    private BigDecimal average(BigDecimal sum, int count) {
        if (count == 0) {
            return null;
        }
        return sum.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
    }
}
