package com.timmk22.smartfarming.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.Schema;
import com.timmk22.smartfarming.dto.response.PlantDiseaseDiagnosisResponse;
import com.timmk22.smartfarming.enumeration.PlantDiseaseDetectionStatus;
import com.timmk22.smartfarming.service.PlantDiseaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PlantDiseaseServiceImpl implements PlantDiseaseService {

    @Value("${app.ai.diagnosis-system-prompt}")
    private String diagnosisSystemPrompt;

    private final GoogleGenAiChatModel chatModel;
    private final Schema diseaseRecognitionSchema;
    private final ObjectMapper objectMapper;

    @Override
    public PlantDiseaseDiagnosisResponse analyzeImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }

        try {
            MimeType mimeType = resolveMimeType(image);
            ByteArrayResource resource = new ByteArrayResource(image.getBytes());

            UserMessage userMessage = UserMessage.builder()
                    .text(diagnosisSystemPrompt)
                    .media(List.of(new Media(mimeType, resource)))
                    .build();

            GoogleGenAiChatOptions chatOptions = GoogleGenAiChatOptions.builder()
                    .responseMimeType("application/json")
                    .responseSchema(diseaseRecognitionSchema.toJson())
                    .build();

            Prompt prompt = new Prompt(List.of(userMessage), chatOptions);

            ChatResponse response = this.chatModel.call(prompt);

            String rawResponse = response.getResult().getOutput().getText();
            return parseResponse(rawResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze image with AI", e);
        }
    }

    private PlantDiseaseDiagnosisResponse parseResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            String statusRaw = root.path("status").asText("");
            String analysis = root.path("analysis").asText("").trim();

            PlantDiseaseDetectionStatus status = parseStatus(statusRaw);
            if (analysis.isBlank()) {
                analysis = "No disease recognition analysis available.";
            }

            return new PlantDiseaseDiagnosisResponse(status, analysis);
        } catch (Exception e) {
            return new PlantDiseaseDiagnosisResponse(
                    PlantDiseaseDetectionStatus.UNRECOGNIZABLE,
                    rawResponse != null ? rawResponse.trim() : "Empty response"
            );
        }
    }

    private PlantDiseaseDetectionStatus parseStatus(String statusRaw) {
        String normalized = statusRaw == null ? "" : statusRaw.trim().toUpperCase(Locale.ROOT);
        if (normalized.contains("DISEASE_NOT_FOUND")) {
            return PlantDiseaseDetectionStatus.DISEASE_NOT_FOUND;
        }
        if (normalized.contains("DISEASE_FOUND")) {
            return PlantDiseaseDetectionStatus.DISEASE_FOUND;
        }
        return PlantDiseaseDetectionStatus.UNRECOGNIZABLE;
    }

    private MimeType resolveMimeType(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return MimeTypeUtils.IMAGE_JPEG;
        }
        return MimeTypeUtils.parseMimeType(contentType);
    }
}
