package at.daniel.phishingprototype.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    private final RestClient restClient;
    private final String model;

    private final int maxAttempts;
    private final long initialDelayMs;


    public LlmService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model,
            @Value("${gemini.base-url}") String baseUrl,
            @Value("${gemini.retry.max-attempts:4}") int maxAttempts,
            @Value("${gemini.retry.initial-delay-ms:1000}") long initialDelayMs) {

        this.model = model;
        this.maxAttempts = maxAttempts;
        this.initialDelayMs = initialDelayMs;

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

        // Retries for a maximum of like 4 attempts because the first attempts often throw up error
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            try {

                return callGemini(prompt);

            } catch (RestClientResponseException e) {

                int statusCode =
                        e.getStatusCode().value();

                if (!isRetryable(statusCode)
                        || attempt == maxAttempts) {

                    throw e;
                }

                sleep(calculateDelay(attempt));
            }
        }

        throw new IllegalStateException(
                "Generation failed after all retry attempts."
        );
    }


    private String callGemini(String prompt) {

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

        JsonNode candidates =
                response.path("candidates");

        if (!candidates.isArray()
                || candidates.size() == 0) {

            throw new IllegalStateException(
                    "Gemini returned no response candidates."
            );
        }

        JsonNode parts = candidates
                .get(0)
                .path("content")
                .path("parts");

        StringBuilder result =
                new StringBuilder();

        for (JsonNode part : parts) {

            String text =
                    part.path("text").asText();

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


    private boolean isRetryable(int statusCode) {

        return statusCode == 429
                || statusCode == 500
                || statusCode == 502
                || statusCode == 503
                || statusCode == 504;
    }


    private long calculateDelay(int attempt) {

        return initialDelayMs
                * (1L << (attempt - 1));
    }


    private void sleep(long delayMs) {

        try {

            Thread.sleep(delayMs);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Generation retry was interrupted.",
                    e
            );
        }
    }
}