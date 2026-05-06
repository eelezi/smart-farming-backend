package com.timmk22.smartfarming.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.Schema;
import com.timmk22.smartfarming.dto.response.RecommendationResponse;
import com.timmk22.smartfarming.dto.response.WeatherResponse;
import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.enumeration.IrrigationType;
import com.timmk22.smartfarming.model.Crop;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.model.Recommendation;
import com.timmk22.smartfarming.model.SoilType;
import com.timmk22.smartfarming.repository.PlantingInformationRepository;
import com.timmk22.smartfarming.repository.RecommendationRepository;
import com.timmk22.smartfarming.service.WeatherService;
import com.timmk22.smartfarming.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ai.chat.prompt.Prompt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RecommendationServiceImplTest {

    private PlantingInformationRepository plantingInformationRepository;
    private WeatherService weatherService;
    private RecommendationRepository recommendationRepository;
    private GoogleGenAiChatModel chatModel;
    private Schema recommendationSchema;
    private ObjectMapper objectMapper;

    private RecommendationServiceImpl recommendationService;

    @BeforeEach
    void setUp() {
        plantingInformationRepository = mock(PlantingInformationRepository.class);
        weatherService = mock(WeatherService.class);
        recommendationRepository = mock(RecommendationRepository.class);
        chatModel = mock(GoogleGenAiChatModel.class);
        recommendationSchema = mock(Schema.class);
        objectMapper = mock(ObjectMapper.class);

        recommendationService = new RecommendationServiceImpl(
                plantingInformationRepository,
                weatherService,
                recommendationRepository,
                chatModel,
                recommendationSchema,
                objectMapper
        );

        ReflectionTestUtils.setField(
                recommendationService,
                "recommendationPrompt",
                "You are an agricultural recommendation system."
        );
    }

    private PlantingInformation createPlanting() {
        Crop crop = new Crop();
        crop.setCropId(2L);
        crop.setName("Tomato");

        SoilType soilType = new SoilType();
        soilType.setSoilId(3L);
        soilType.setName("Loamy");

        PlantingInformation planting = new PlantingInformation();
        planting.setPlantingId(1L);
        planting.setCrop(crop);
        planting.setSoilType(soilType);
        planting.setLatitude(41.63);
        planting.setLongitude(22.47);
        planting.setPlantingDate(LocalDate.of(2026, 5, 1));
        planting.setIrrigationType(IrrigationType.DRIP);
        planting.setCurrentStatus(CurrentStatus.HEALTHY);
        planting.setNotes("Healthy crop");
        return planting;
    }

    private WeatherResponse createWeatherResponse() {
        WeatherResponse weather = new WeatherResponse();
        weather.setLatitude(new BigDecimal("41.63"));
        weather.setLongitude(new BigDecimal("22.47"));
        weather.setTimezone("UTC");
        weather.setForecastDays(7);
        weather.setTime(LocalDateTime.of(2026, 5, 6, 0, 0));
        weather.setTemp2mMax(new BigDecimal("28.50"));
        weather.setTemp2mMin(new BigDecimal("14.20"));
        weather.setSunrise(LocalDateTime.of(2026, 5, 6, 5, 10));
        weather.setSunset(LocalDateTime.of(2026, 5, 6, 19, 30));
        weather.setPercProbMax(new BigDecimal("60.00"));
        weather.setRainSum(new BigDecimal("12.30"));
        weather.setShowersSum(new BigDecimal("3.40"));
        weather.setSnowfallSum(new BigDecimal("0.00"));
        return weather;
    }

    @Test
    @DisplayName("Should generate recommendation successfully with weather data")
    void shouldGenerateRecommendationSuccessfullyWithWeatherData() throws Exception {
        PlantingInformation planting = createPlanting();

        Recommendation firstSave = new Recommendation();
        firstSave.setRecommendationId(100L);
        firstSave.setCreatedAt(LocalDateTime.now());
        firstSave.setPlantingInformation(planting);
        firstSave.setRecommendationText("Temporary text");

        Recommendation finalSave = new Recommendation();
        finalSave.setRecommendationId(100L);
        finalSave.setCreatedAt(firstSave.getCreatedAt());
        finalSave.setPlantingInformation(planting);
        finalSave.setRecommendationText("Irrigate lightly in the early morning.");

        when(plantingInformationRepository.findById(1L)).thenReturn(Optional.of(planting));
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenReturn(firstSave)
                .thenReturn(finalSave);

        ChatResponse chatResponse = new ChatResponse(List.of(
                new Generation(new AssistantMessage("{\"recommendationText\":\"Irrigate lightly in the early morning.\"}"))
        ));
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        JsonNode rootNode = mock(JsonNode.class);
        JsonNode recommendationTextNode = mock(JsonNode.class);

        when(objectMapper.readTree("{\"recommendationText\":\"Irrigate lightly in the early morning.\"}"))
                .thenReturn(rootNode);
        when(rootNode.path("recommendationText")).thenReturn(recommendationTextNode);
        when(recommendationTextNode.asText()).thenReturn("Irrigate lightly in the early morning.");

        when(weatherService.getWeather(
                eq(BigDecimal.valueOf(41.63)),
                eq(BigDecimal.valueOf(22.47)),
                eq("UTC"),
                isNull()
        )).thenReturn(List.of(createWeatherResponse()));

        RecommendationResponse response = recommendationService.generateRecommendation(1L, false);

        assertThat(response.getRecommendationId()).isEqualTo(100L);
        assertThat(response.getRecommendationText()).isEqualTo("Irrigate lightly in the early morning.");
        assertThat(response.getPlantingId()).isEqualTo(1L);
        assertThat(response.getCreatedAt()).isNotNull();

        verify(plantingInformationRepository).findById(1L);
        verify(recommendationRepository, times(2)).save(any(Recommendation.class));
        verify(weatherService).getWeather(
                eq(BigDecimal.valueOf(41.63)),
                eq(BigDecimal.valueOf(22.47)),
                eq("UTC"),
                isNull()
        );
        verify(chatModel).call(any(Prompt.class));
    }

    @Test
    @DisplayName("Should generate recommendation even when weather service fails")
    void shouldGenerateRecommendationEvenWhenWeatherServiceFails() throws Exception {
        PlantingInformation planting = createPlanting();

        Recommendation firstSave = new Recommendation();
        firstSave.setRecommendationId(101L);
        firstSave.setCreatedAt(LocalDateTime.now());
        firstSave.setPlantingInformation(planting);
        firstSave.setRecommendationText("Temporary text");

        Recommendation finalSave = new Recommendation();
        finalSave.setRecommendationId(101L);
        finalSave.setCreatedAt(firstSave.getCreatedAt());
        finalSave.setPlantingInformation(planting);
        finalSave.setRecommendationText("Monitor soil moisture and continue regular care.");

        when(plantingInformationRepository.findById(1L)).thenReturn(Optional.of(planting));
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenReturn(firstSave)
                .thenReturn(finalSave);

        when(weatherService.getWeather(
                eq(BigDecimal.valueOf(41.63)),
                eq(BigDecimal.valueOf(22.47)),
                eq("UTC"),
                eq(101L)
        )).thenThrow(new RuntimeException("Weather API unavailable"));

        ChatResponse chatResponse = new ChatResponse(List.of(
                new Generation(new AssistantMessage("{\"recommendationText\":\"Monitor soil moisture and continue regular care.\"}"))
        ));
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        JsonNode rootNode = mock(JsonNode.class);
        JsonNode recommendationTextNode = mock(JsonNode.class);

        when(objectMapper.readTree("{\"recommendationText\":\"Monitor soil moisture and continue regular care.\"}"))
                .thenReturn(rootNode);
        when(rootNode.path("recommendationText")).thenReturn(recommendationTextNode);
        when(recommendationTextNode.asText()).thenReturn("Monitor soil moisture and continue regular care.");

        RecommendationResponse response = recommendationService.generateRecommendation(1L, true);

        assertThat(response.getRecommendationId()).isEqualTo(101L);
        assertThat(response.getRecommendationText()).isEqualTo("Monitor soil moisture and continue regular care.");
        assertThat(response.getPlantingId()).isEqualTo(1L);

        verify(chatModel).call(any(Prompt.class));
        verify(recommendationRepository, times(2)).save(any(Recommendation.class));
    }

    @Test
    @DisplayName("Should throw when planting information is not found")
    void shouldThrowWhenPlantingInformationIsNotFound() {
        when(plantingInformationRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> recommendationService.generateRecommendation(99L, false)
        );

        assertThat(ex.getMessage()).isEqualTo("PlantingInformation not found for id: 99");

        verify(plantingInformationRepository).findById(99L);
        verifyNoInteractions(weatherService, recommendationRepository, chatModel, objectMapper);
    }

    @Test
    @DisplayName("Should throw when AI response cannot be parsed")
    void shouldThrowWhenAiResponseCannotBeParsed() throws Exception {
        PlantingInformation planting = createPlanting();

        Recommendation firstSave = new Recommendation();
        firstSave.setRecommendationId(102L);
        firstSave.setCreatedAt(LocalDateTime.now());
        firstSave.setPlantingInformation(planting);
        firstSave.setRecommendationText("Temporary text");

        when(plantingInformationRepository.findById(1L)).thenReturn(Optional.of(planting));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(firstSave);
        when(weatherService.getWeather(any(), any(), anyString(), anyLong())).thenReturn(List.of(createWeatherResponse()));

        ChatResponse chatResponse = new ChatResponse(List.of(
                new Generation(new AssistantMessage("not-json"))
        ));
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        when(objectMapper.readTree("not-json")).thenThrow(new RuntimeException("Bad JSON"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> recommendationService.generateRecommendation(1L, false)
        );

        assertThat(ex.getMessage()).contains("Failed to generate AI recommendation");
    }

    @Test
    @DisplayName("Should save recommendation twice during generation")
    void shouldSaveRecommendationTwiceDuringGeneration() throws Exception {
        PlantingInformation planting = createPlanting();

        Recommendation firstSave = new Recommendation();
        firstSave.setRecommendationId(103L);
        firstSave.setCreatedAt(LocalDateTime.now());
        firstSave.setPlantingInformation(planting);
        firstSave.setRecommendationText("Temporary text");

        Recommendation finalSave = new Recommendation();
        finalSave.setRecommendationId(103L);
        finalSave.setCreatedAt(firstSave.getCreatedAt());
        finalSave.setPlantingInformation(planting);
        finalSave.setRecommendationText("Use balanced irrigation this week.");

        when(plantingInformationRepository.findById(1L)).thenReturn(Optional.of(planting));
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenReturn(firstSave)
                .thenReturn(finalSave);

        when(weatherService.getWeather(any(), any(), anyString(), anyLong()))
                .thenReturn(List.of(createWeatherResponse()));

        ChatResponse chatResponse = new ChatResponse(List.of(
                new Generation(new AssistantMessage("{\"recommendationText\":\"Use balanced irrigation this week.\"}"))
        ));
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        JsonNode rootNode = mock(JsonNode.class);
        JsonNode recommendationTextNode = mock(JsonNode.class);

        when(objectMapper.readTree("{\"recommendationText\":\"Use balanced irrigation this week.\"}"))
                .thenReturn(rootNode);
        when(rootNode.path("recommendationText")).thenReturn(recommendationTextNode);
        when(recommendationTextNode.asText()).thenReturn("Use balanced irrigation this week.");

        RecommendationResponse response = recommendationService.generateRecommendation(1L, false);

        verify(recommendationRepository, times(2)).save(any(Recommendation.class));
        assertThat(response.getRecommendationText()).isEqualTo("Use balanced irrigation this week.");
    }
}