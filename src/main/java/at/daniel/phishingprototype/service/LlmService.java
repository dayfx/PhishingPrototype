package at.daniel.phishingprototype.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    private final RestClient restClient;
    private final String model;

    public LlmService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model,
            @Value("${gemini.base-url}") String baseUrl) {

        this.model = model;

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader(
                        "Content-Type",
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }


    public String generate(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of(
                                                "text", prompt
                                        )
                                )
                        )
                )
        );

        JsonNode response = restClient
                .post()
                .uri(
                        "/v1beta/models/{model}:generateContent",
                        model
                )
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Gemini returned an empty response."
            );
        }

        return extractOutputText(response);
    }


    private String extractOutputText(JsonNode response) {

        JsonNode candidates = response.path("candidates");

        if (!candidates.isArray() || candidates.size() == 0) {
            throw new IllegalStateException(
                    "Gemini returned no response candidates."
            );
        }

        JsonNode parts = candidates
                .get(0)
                .path("content")
                .path("parts");

        StringBuilder result = new StringBuilder();

        for (JsonNode part : parts) {

            String text = part
                    .path("text")
                    .asText();

            if (!text.isBlank()) {

                if (!result.isEmpty()) {
                    result.append("\n");
                }

                result.append(text);
            }
        }

        if (result.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini returned no generated text."
            );
        }

        return result.toString();
    }
}