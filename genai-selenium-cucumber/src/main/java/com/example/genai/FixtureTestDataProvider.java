package com.example.genai;

import java.util.List;

public final class FixtureTestDataProvider implements AiTestDataProvider {
    private static final List<String> FIXTURES = List.of(
            "Avery QA 001",
            "Morgan Edge 002",
            "QA O'Connor & Sons 42");

    @Override
    public List<String> generateSyntheticNames(int count) {
        if (count < 1 || count > FIXTURES.size()) {
            throw new IllegalArgumentException("Fixture provider supports 1 to " + FIXTURES.size() + " names");
        }
        return FIXTURES.subList(0, count);
    }

    @Override
    public String providerName() {
        return "fixture";
    }
}
