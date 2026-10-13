package com.example.genai;

public final class AiTestDataProviderFactory {
    private AiTestDataProviderFactory() {
    }

    public static AiTestDataProvider fromEnvironment() {
        String apiKey = System.getenv("GENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return new FixtureTestDataProvider();
        }

        String baseUrl = environmentOrDefault("GENAI_BASE_URL", "https://api.openai.com/v1");
        String model = environmentOrDefault("GENAI_MODEL", "gpt-4o-mini");
        return new OpenAiCompatibleTestDataProvider(baseUrl, apiKey, model);
    }

    private static String environmentOrDefault(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
