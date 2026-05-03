package com.timmk22.smartfarming.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.Schema;
import com.timmk22.smartfarming.dto.response.EntryAiTipsResponse;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.repository.PlantingInformationRepository;
import com.timmk22.smartfarming.service.EntryAiTipsService;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntryAiTipsServiceImpl implements EntryAiTipsService {

    @Value("${app.ai.entry-tips-system-prompt:You are an expert agricultural advisor. Analyze the planting information provided and generate personalized cultivation tips, practical instructions, and advice based on the crop type, soil conditions, and current status.}")
    private String entryTipsSystemPrompt;

    private final PlantingInformationRepository plantingInformationRepository;
    private final GoogleGenAiChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final Schema entryTipsSchema;

    public EntryAiTipsServiceImpl(PlantingInformationRepository plantingInformationRepository,
                                 GoogleGenAiChatModel chatModel,
                                 ObjectMapper objectMapper,
                                 @Qualifier("entryTipsSchema") Schema entryTipsSchema) {
        this.plantingInformationRepository = plantingInformationRepository;
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
        this.entryTipsSchema = entryTipsSchema;
    }

    @Override
    public EntryAiTipsResponse generateAiTips(Long entryId, Long userId) {
        // Verify entry exists and belongs to user
        PlantingInformation entry = plantingInformationRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found with ID: " + entryId));

        if (!entry.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to entry with ID: " + entryId);
        }

        try {
            String prompt = buildPrompt(entry);
            UserMessage userMessage = UserMessage.builder()
                    .text(prompt)
                    .build();

            GoogleGenAiChatOptions chatOptions = GoogleGenAiChatOptions.builder()
                    .responseMimeType("application/json")
                    .responseSchema(entryTipsSchema.toJson())
                    .build();

            Prompt aiPrompt = new Prompt(List.of(userMessage), chatOptions);
            ChatResponse response = this.chatModel.call(aiPrompt);

            String rawResponse = response.getResult().getOutput().getText();
            return parseResponse(rawResponse);
        } catch (Exception e) {
            // Graceful error handling: return a default response if AI service fails
            return buildFallbackResponse(entry);
        }
    }

    private String buildPrompt(PlantingInformation entry) {
        long daysSincePlanting = ChronoUnit.DAYS.between(entry.getPlantingDate(), LocalDate.now());
        long daysUntilHarvest = entry.getExpectedHarvestDate() != null
                ? ChronoUnit.DAYS.between(LocalDate.now(), entry.getExpectedHarvestDate())
                : -1;

        return entryTipsSystemPrompt + "\n\n" +
                "Planting Information:\n" +
                "- Crop: " + entry.getCrop().getName() + "\n" +
                "- Area: " + entry.getArea() + " hectares\n" +
                "- Soil Type: " + entry.getSoilType().getName() + "\n" +
                "- Irrigation Type: " + entry.getIrrigationType() + "\n" +
                "- Current Status: " + entry.getCurrentStatus() + "\n" +
                "- Days Since Planting: " + daysSincePlanting + "\n" +
                "- Days Until Expected Harvest: " + daysUntilHarvest + "\n" +
                "- Location: " + (entry.getLocationName() != null ? entry.getLocationName() : "Not specified") + "\n" +
                "- Notes: " + (entry.getNotes() != null ? entry.getNotes() : "None") + "\n\n" +
                "Please provide specific, actionable tips and instructions for cultivating this crop based on the current conditions.";
    }

    private EntryAiTipsResponse parseResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            String summary = root.path("summary").asText("Comprehensive cultivation advice has been generated.");
            String cultivationAdvice = root.path("cultivationAdvice").asText("");

            List<String> tips = new ArrayList<>();
            JsonNode tipsNode = root.path("tips");
            if (tipsNode.isArray()) {
                tipsNode.forEach(tip -> tips.add(tip.asText()));
            }

            List<String> instructions = new ArrayList<>();
            JsonNode instructionsNode = root.path("instructions");
            if (instructionsNode.isArray()) {
                instructionsNode.forEach(instruction -> instructions.add(instruction.asText()));
            }

            return new EntryAiTipsResponse(summary, tips, instructions, cultivationAdvice);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private EntryAiTipsResponse buildFallbackResponse(PlantingInformation entry) {
        List<String> tips = List.of(
                "Monitor soil moisture regularly - adjust irrigation based on current weather conditions",
                "Check for pest and disease signs daily, especially on leaves and stems",
                "Maintain consistent care routine for optimal crop development",
                "Document any changes in crop status for future reference"
        );

        List<String> instructions = List.of(
                "Review weather forecasts regularly and adjust irrigation schedule accordingly",
                "Apply recommended fertilizers based on crop development stage",
                "Keep the planting area clear of weeds to reduce competition for nutrients",
                "Contact local agricultural extension services for specific crop advisories"
        );

        String summary = "AI service is temporarily unavailable. General cultivation guidelines have been provided. " +
                "Consult local agricultural experts for specific recommendations for " + entry.getCrop().getName() + ".";

        String cultivationAdvice = "For " + entry.getCrop().getName() + " grown in " +
                entry.getSoilType().getName() + " soil with " + entry.getIrrigationType() + " irrigation: " +
                "Focus on consistent watering, pest monitoring, and timely nutrient management. " +
                "Current status shows the crop is " + entry.getCurrentStatus().toString().toLowerCase() + ".";

        return new EntryAiTipsResponse(summary, tips, instructions, cultivationAdvice);
    }
}
