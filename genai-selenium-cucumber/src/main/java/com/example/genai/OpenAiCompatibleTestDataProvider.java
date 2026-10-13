package com.example.genai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class OpenAiCompatibleTestDataProvider implements AiTestDataProvider {
    private static final ObjectMapper JSON = new ObjectMapper();
    private final HttpClient httpClient;
    private final String baseUrl;
    private final String apiKey;
    private final String model;

    public OpenAiCompatibleTestDataProvider(String baseUrl, String apiKey, String model) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public List<String> generateSyntheticNames(int count) throws Exception {
        if (count < 1 || count > 10) {
            throw new IllegalArgumentException("Requested name count must be between 1 and 10");
        }

        String prompt = "Return exactly " + count + " distinct synthetic names for automated UI testing. "
                + "Use only fictional test data; do not use real people's personal information. "
                + "Include a few safe punctuation characters when appropriate. "
                + "Respond only with a JSON array of strings, without markdown.";
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "temperature", 0.4,
                "messages", List.of(
                        Map.of("role", "system", "content", "You generate synthetic, non-sensitive UI test data."),
                        Map.of("role", "user", "content", prompt)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .timeout(Duration.ofSeconds(45))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("GenAI endpoint returned HTTP " + response.statusCode()
                    + ": " + response.body());
        }

        JsonNode root = JSON.readTree(response.body());
        String content = root.path("choices").path(0).path("message").path("content").asText();
        List<String> names = parseNames(content);
        if (names.size() != count) {
            throw new IllegalStateException("GenAI endpoint returned " + names.size()
                    + " names; expected " + count);
        }
        if (names.stream().anyMatch(String::isBlank)) {
            throw new IllegalStateException("GenAI endpoint returned a blank test value");
        }
        return names;
    }

    @Override
    public String providerName() {
        return "OpenAI-compatible endpoint (" + model + ")";
    }

    private static List<String> parseNames(String content) throws Exception {
        String normalized = content.trim();
        if (normalized.startsWith("```")) {
            normalized = normalized.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
        }
        JsonNode values = JSON.readTree(normalized);
        if (!values.isArray()) {
            throw new IllegalStateException("GenAI response must be a JSON array of strings");
        }

        List<String> names = new ArrayList<>();
        for (JsonNode value : values) {
            if (!value.isTextual()) {
                throw new IllegalStateException("Every generated test value must be a string");
            }
            names.add(value.asText().trim());
        }
        return names;
    }

    private static String trimTrailingSlash(String value) {
        String result = value.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
