package com.example.genai;

import io.cucumber.core.cli.Main;

public final class CucumberRunner {
    private CucumberRunner() {
    }

    public static void main(String[] args) {
        byte exitStatus = Main.run(
                new String[]{
                        "--glue", "com.example.genai",
                        "--plugin", "pretty",
                        "classpath:features"
                },
                Thread.currentThread().getContextClassLoader());
        if (exitStatus != 0) {
            System.exit(exitStatus);
        }
    }
}
