package com.timmk22.smartfarming.web.config;

import com.google.genai.Client;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class AiConfig {

    @Value("${spring.ai.google.genai.api-key}")
    private String apiKey;

    @Value("${spring.ai.google.genai.chat.options.model:gemini-3-flash-preview}")
    private String modelName;

    @Bean
    public Client googleGenAiClient() {
        return Client.builder()
                .apiKey(apiKey)
                .build();
    }

    @Bean
    public GoogleGenAiChatModel googleGenAiChatModel(Client googleGenAiClient) {
        return GoogleGenAiChatModel.builder()
                .genAiClient(googleGenAiClient)
                .defaultOptions(GoogleGenAiChatOptions.builder()
                        .model(modelName)
                        .temperature(0.0)
                        .build())
                .build();
    }

    @Bean
    public Schema diseaseRecognitionSchema() {
        return Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "status", Schema.builder()
                                .type(Type.Known.STRING)
                                .enum_(List.of("DISEASE_FOUND", "DISEASE_NOT_FOUND", "UNRECOGNIZABLE"))
                                .description("One of the configured enumeration values based on whether disease is detected.")
                                .build(),
                        "analysis", Schema.builder()
                                .type(Type.Known.STRING)
                                .description("A short explanation of what was detected on the image. If a disease is detected, provide the name of the disease, the symptoms that led to your conclusion and a brief suggestion on how it should be treated if possible.")
                                .build()
                ))
                .required(List.of("status", "analysis"))
                .build();
    }

    @Bean
    public Schema recommendationSchema() {
        return Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "recommendationText", Schema.builder()
                                .type(Type.Known.STRING)
                                .description("The main text containing the crop recommendation/suggestions for irrigation, and general tips for the specific crop and what to do in case of unexpected weather/temperature change.")
                                .build()
                ))
                .required(List.of("recommendationText"))
                .build();
    }
}
