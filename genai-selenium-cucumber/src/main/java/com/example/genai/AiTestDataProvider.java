package com.example.genai;

import java.util.List;

public interface AiTestDataProvider {
    List<String> generateSyntheticNames(int count) throws Exception;

    String providerName();
}
