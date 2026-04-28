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
    public RecommendationResponse generateRecommendation(Long plantingId) {
        PlantingInformation plantingInfo = plantingInformationRepository.findById(plantingId)
                .orElseThrow(() -> new RuntimeException("PlantingInformation not found for id: " + plantingId));

        Recommendation recommendation = new Recommendation();
        recommendation.setCreatedAt(LocalDateTime.now());
        recommendation.setPlantingInformation(plantingInfo);
        recommendation.setRecommendationText("Temporary text");
        recommendationRepository.save(recommendation);

        WeatherResponse weatherResponse = null;
        try {
            BigDecimal lat = BigDecimal.valueOf(plantingInfo.getLatitude());
            BigDecimal lon = BigDecimal.valueOf(plantingInfo.getLongitude());
            weatherResponse = weatherService.getWeather(lat, lon, "UTC", recommendation.getRecommendationId()); // TODO: Change to dynamic based on user
        } catch (Exception e) {
            // Continue without weather data
        }

        SystemMessage systemMessage = new SystemMessage(recommendationPrompt);

        String contextString = buildContextString(plantingInfo, weatherResponse);

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

    private String buildContextString(PlantingInformation info, WeatherResponse weather) {
        StringBuilder sb = new StringBuilder();
        sb.append("Planting Information:\n");
        sb.append("Crop Name: ").append(info.getCrop().getName()).append("\n");
        sb.append("Irrigation Type: ").append(info.getIrrigationType() != null ? info.getIrrigationType().name() : "N/A").append("\n");
        sb.append("Current Status: ").append(info.getCurrentStatus() != null ? info.getCurrentStatus().name() : "N/A").append("\n");
        sb.append("Planting Date: ").append(info.getPlantingDate() != null ? info.getPlantingDate().toString() : "N/A").append("\n");

        sb.append("\nWeather Forecast Data:\n");
        if (weather != null) {
            sb.append("Latitude: ").append(weather.getLatitude()).append("\n");
            sb.append("Longitude: ").append(weather.getLongitude()).append("\n");
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
            sb.append("Hourly Time: ").append(weather.getHourlyTime()).append("\n");
            sb.append("Temp 2m (C): ").append(weather.getTemp2m()).append("\n");
            sb.append("Relative Humidity 2m (%): ").append(weather.getRelatHum2m()).append("\n");
            sb.append("Cloud Cover (%): ").append(weather.getCloudCover()).append("\n");
            sb.append("Wind Speed 10m: ").append(weather.getWindSpeed10m()).append("\n");
            sb.append("Soil Moisture 9-27cm: ").append(weather.getSoilMoisture9To27cm()).append("\n");
            sb.append("Direct Normal Irradiance: ").append(weather.getDirectNormIrradiance()).append("\n");
            sb.append("Vapour Pressure Deficit: ").append(weather.getVapourPressureDeficit()).append("\n");
            sb.append("Evapotranspiration: ").append(weather.getEvapotranspiration()).append("\n");
        } else {
            sb.append("missing data\n");
        }

        return sb.toString();
    }
}
