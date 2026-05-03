package com.timmk22.smartfarming.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmk22.smartfarming.dto.GeneratePdfRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenAiHealthSummaryService implements HealthSummaryService {

    private final GoogleGenAiChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Value("${app.ai.health-summary-prompt:You are an expert agricultural AI. Based on the provided farm statistics, write a professional, concise summary of the farm's health in plain text. Focus on disease risk, moisture levels, and any fields that need immediate attention. Do not use markdown. Do not use emojis or special unicode characters.}")
    private String healthSummaryPrompt;

    @Override
    public String generateSummary(GeneratePdfRequest request) {
        String statsJson;
        try {
            statsJson = objectMapper.writeValueAsString(request.getStats());
        } catch (JsonProcessingException e) {
            statsJson = request.getStats().toString();
        }

        String userContent = healthSummaryPrompt + "\n\nFarm Name: " + request.getFarmName() + "\nStats:\n" + statsJson;

        UserMessage userMessage = new UserMessage(userContent);

        GoogleGenAiChatOptions chatOptions = GoogleGenAiChatOptions.builder()
                .build();

        Prompt prompt = new Prompt(List.of(userMessage), chatOptions);

        try {
            ChatResponse response = this.chatModel.call(prompt);
            return response.getResult().getOutput().getText().trim();
        } catch (Exception e) {
             return "AI Summary unavailable due to an error: " + e.getMessage();
        }
    }
}

