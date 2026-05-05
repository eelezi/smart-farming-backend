package com.timmk22.smartfarming.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.service.HealthSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenAiHealthSummaryService implements HealthSummaryService {

    private final GoogleGenAiChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Value("${app.ai.health-summary-prompt:You are an expert agricultural AI. Based on the provided farm statistics, write a professional, concise summary of the farm's health in plain text. Focus on disease risk, moisture levels, and any fields that need immediate attention. Do not use markdown. Do not use emojis or special unicode characters.}")
    private String healthSummaryPrompt;

    @Override
    public String generateSummary(String farmName, List<PlantingInformation> plantings) {
        StringBuilder plantingDetails = new StringBuilder();
        
        // Build a detailed context from PlantingInformation records
        for (PlantingInformation planting : plantings) {
            plantingDetails.append("- Crop: ").append(planting.getCrop().getName());
            plantingDetails.append(", Field: ").append(planting.getLocationName() != null ? planting.getLocationName() : "Field-" + planting.getPlantingId());
            plantingDetails.append(", Area: ").append(planting.getArea()).append(" acres");
            plantingDetails.append(", Status: ").append(planting.getCurrentStatus());
            plantingDetails.append(", Irrigation: ").append(planting.getIrrigationType());
            plantingDetails.append("\n");
        }

        String userContent = healthSummaryPrompt + 
                "\n\nFarm Name: " + farmName + 
                "\nPlanting Information:\n" + 
                plantingDetails.toString();

        UserMessage userMessage = new UserMessage(userContent);

        GoogleGenAiChatOptions chatOptions = GoogleGenAiChatOptions.builder()
                .build();

        Prompt prompt = new Prompt(List.of(userMessage), chatOptions);

        try {
            ChatResponse response = this.chatModel.call(prompt);
            return response.getResult().getOutput().getText().trim();
        } catch (Exception e) {
            log.error("Failed to generate AI health summary for farm '{}'", farmName, e);
            return "AI health summary is currently unavailable. Please try again later.";
        }
    }
}

