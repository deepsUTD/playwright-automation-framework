package com.example.herokuapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates an interactive, styled HTML test report mirroring the Playwright custom reporter.
 * Provides a tabular summary with clickable test links, per-test step breakdowns,
 * and failure screenshots associated with the failed steps.
 */
final class TestRunReport {
    private static final Path ROOT_REPORT_DIR = Path.of("custom-report");
    private static final Path TARGET_REPORT_DIR = Path.of("target", "custom-report");
    private static final Path SUREFIRE_REPORT_PATH = Path.of("target", "reports", "test-screenshots.html");
    private static final Path SCREENSHOT_SRC_DIR = Path.of("target", "screenshots");

    private static final Map<String, TestRecord> RESULTS = new LinkedHashMap<>();

    private static final String SHARED_CSS = """
        :root { color-scheme: light; font: 15px/1.5 Segoe UI, Arial, sans-serif; color: #1f2937; background: #f3f4f6; }
        body { margin: 0; padding: 2rem; }
        main { max-width: 1100px; margin: auto; background: white; padding: 2rem; border-radius: 10px; box-shadow: 0 2px 12px #0001; }
        h1, h2, h3 { line-height: 1.2; }
        table { width: 100%; border-collapse: collapse; margin-top: 1.5rem; }
        th, td { padding: .7rem; border-bottom: 1px solid #e5e7eb; text-align: left; vertical-align: top; }
        th { background: #f9fafb; }
        a { color: #075bbb; text-decoration: none; }
        a:hover { text-decoration: underline; }
        .badge { display: inline-block; padding: .1rem .55rem; border-radius: 999px; font-weight: 700; text-transform: uppercase; font-size: .75rem; }
        .passed, .pass { color: #166534; background: #dcfce7; }
        .failed, .fail { color: #991b1b; background: #fee2e2; }
        .flaky { color: #854d0e; background: #fef3c7; }
        .skipped, .aborted, .disabled { color: #374151; background: #e5e7eb; }
        .running { color: #1d4ed8; background: #dbeafe; }
        .summary { display: flex; gap: 1rem; flex-wrap: wrap; margin-top: 1rem; }
        .summary span { background: #f9fafb; border: 1px solid #e5e7eb; padding: .5rem .8rem; border-radius: 6px; }
        .steps { padding-left: 1.4rem; }
        .steps li { margin: .6rem 0; }
        summary { display: flex; align-items: center; gap: .6rem; cursor: pointer; }
        summary small { color: #6b7280; margin-left: auto; }
        pre { white-space: pre-wrap; overflow-wrap: anywhere; padding: 1rem; border-radius: 6px; background: #f9fafb; }
        pre.error { color: #991b1b; background: #fff1f2; border: 1px solid #fecaca; }
        .screenshot { max-width: 100%; border: 1px solid #d1d5db; border-radius: 6px; box-shadow: 0 2px 8px #00000012; }
        .artifact { margin: 1.5rem 0 2rem; }
        .muted { color: #6b7280; }
        @media (max-width: 700px) { body { padding: .5rem; } main { padding: 1rem; overflow-x: auto; } }
    """;

    private TestRunReport() {
    }

    public static synchronized TestRecord getOrCreate(String testName) {
        return RESULTS.computeIfAbsent(testName, key -> {
            String className = key.contains("#") ? key.substring(0, key.indexOf('#')) : key;
            String methodName = key.contains("#") ? key.substring(key.indexOf('#') + 1) : key;
            return new TestRecord(key, className, methodName, methodName);
        });
    }

    public static synchronized void startTest(String testName, String className, String methodName, String displayName) {
        TestRecord record = RESULTS.computeIfAbsent(testName, k -> new TestRecord(testName, className, methodName, displayName));
        record.startTime = System.currentTimeMillis();
    }

    public static synchronized void startStep(String testName, String stepTitle) {
        TestRecord record = getOrCreate(testName);
        record.addStep(new StepInfo(stepTitle));
    }

    public static synchronized void completeStep(String testName, String stepTitle, String status, long durationMs, String error) {
        TestRecord record = getOrCreate(testName);
        record.updateStep(stepTitle, normalizeStatus(status), durationMs, error);
    }

    public static synchronized void recordScreenshot(String testName, String screenshotFile, String screenshotError)
            throws IOException {
        TestRecord record = getOrCreate(testName);
        record.screenshotFile = screenshotFile;
        record.screenshotError = screenshotError;
        copyScreenshotAsset(screenshotFile);
        writeReports();
    }

    public static synchronized void recordOutcome(String testName, String status, String failure) throws IOException {
        TestRecord record = getOrCreate(testName);
        record.status = normalizeStatus(status);
        record.failureDetails = failure;
        if (record.durationMs == 0 && record.startTime > 0) {
            record.durationMs = System.currentTimeMillis() - record.startTime;
        }
        writeReports();
    }

    public static String formatThrowable(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    private static String normalizeStatus(String status) {
        if (status == null) return "unknown";
        String s = status.trim().toLowerCase();
        return switch (s) {
            case "pass", "passed", "success", "successful" -> "passed";
            case "fail", "failed", "failure" -> "failed";
            case "aborted", "abort" -> "aborted";
            case "disabled", "skipped", "skip" -> "skipped";
            default -> s;
        };
    }

    static String fileId(String testName) {
        String readable = testName.replaceAll("[^a-zA-Z0-9_-]+", "-");
        if (readable.length() > 48) {
            readable = readable.substring(readable.length() - 48);
        }
        String hash = sha1Hex(testName);
        if (hash.length() > 10) {
            hash = hash.substring(0, 10);
        }
        return readable + "-" + hash;
    }

    private static String sha1Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(input.hashCode());
        }
    }

    private static void copyScreenshotAsset(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        Path source = SCREENSHOT_SRC_DIR.resolve(fileName);
        if (!Files.exists(source)) {
            return;
        }
        try {
            Path rootAssets = ROOT_REPORT_DIR.resolve("assets");
            Files.createDirectories(rootAssets);
            Files.copy(source, rootAssets.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            Path targetAssets = TARGET_REPORT_DIR.resolve("assets");
            Files.createDirectories(targetAssets);
            Files.copy(source, targetAssets.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
            // Ignore asset copy failures during reporting
        }
    }

    private static synchronized void writeReports() throws IOException {
        Files.createDirectories(ROOT_REPORT_DIR.resolve("assets"));
        Files.createDirectories(TARGET_REPORT_DIR.resolve("assets"));
        if (SUREFIRE_REPORT_PATH.getParent() != null) {
            Files.createDirectories(SUREFIRE_REPORT_PATH.getParent());
        }

        List<TestRecord> tests = new ArrayList<>(RESULTS.values());

        // 1. Write per-test detail pages
        for (TestRecord test : tests) {
            String detailHtml = renderTestDetails(test);
            String fileName = fileId(test.id) + ".html";
            Files.writeString(ROOT_REPORT_DIR.resolve(fileName), detailHtml, StandardCharsets.UTF_8);
            Files.writeString(TARGET_REPORT_DIR.resolve(fileName), detailHtml, StandardCharsets.UTF_8);
        }

        // 2. Generate summary counts
        int passed = 0;
        int failed = 0;
        int aborted = 0;
        int skipped = 0;
        for (TestRecord test : tests) {
            switch (test.status) {
                case "passed" -> passed++;
                case "failed" -> failed++;
                case "aborted" -> aborted++;
                case "skipped" -> skipped++;
                default -> {}
            }
        }

        String summaryHtml = String.format(
            "<span><strong>passed:</strong> %d</span>" +
            "<span><strong>failed:</strong> %d</span>" +
            "<span><strong>aborted:</strong> %d</span>" +
            "<span><strong>skipped:</strong> %d</span>" +
            "<span><strong>Total:</strong> %d</span>",
            passed, failed, aborted, skipped, tests.size()
        );

        // 3. Generate table rows
        StringBuilder rows = new StringBuilder();
        for (TestRecord test : tests) {
            String status = test.status;
            String detailLink = fileId(test.id) + ".html";
            String simpleSuite = test.className.contains(".")
                ? test.className.substring(test.className.lastIndexOf('.') + 1)
                : test.className;
            String screenshotStatus = test.screenshotFile != null ? "Available" : (test.screenshotError != null ? "Error" : "None");

            rows.append("<tr>");
            rows.append("<td><span class=\"badge ").append(status).append("\">").append(escapeHtml(status)).append("</span></td>");
            rows.append("<td><a href=\"").append(escapeHtml(detailLink)).append("\">").append(escapeHtml(test.displayName)).append("</a></td>");
            rows.append("<td>").append(escapeHtml(simpleSuite)).append("</td>");
            rows.append("<td>").append(escapeHtml(test.id)).append("</td>");
            rows.append("<td>").append(test.durationMs).append(" ms</td>");
            rows.append("<td>").append(escapeHtml(screenshotStatus)).append("</td>");
            rows.append("</tr>");
        }

        String indexHtml = reportPage("Selenium Custom Test Report", String.format("""
            <h1>Selenium Test Report</h1>
            <p class="muted">Select a test name to view its step-by-step results, failure details, and associated screenshots.</p>
            <div class="summary">%s</div>
            <table>
              <thead>
                <tr>
                  <th>Result</th>
                  <th>Test</th>
                  <th>Suite</th>
                  <th>Source</th>
                  <th>Duration</th>
                  <th>Screenshot</th>
                </tr>
              </thead>
              <tbody>
                %s
              </tbody>
            </table>
        """, summaryHtml, rows.length() > 0 ? rows.toString() : "<tr><td colspan=\"6\">No tests were reported.</td></tr>"));

        Files.writeString(ROOT_REPORT_DIR.resolve("index.html"), indexHtml, StandardCharsets.UTF_8);
        Files.writeString(TARGET_REPORT_DIR.resolve("index.html"), indexHtml, StandardCharsets.UTF_8);

        // Also update legacy surefire reports page with link to custom report
        String legacyHtml = reportPage("Selenium Test Results and Screenshots", String.format("""
            <h1>Test Results and Screenshots</h1>
            <p><a href="../../custom-report/index.html"><strong>→ Open Modern Custom Test Report</strong></a> | <a href="surefire.html">Surefire HTML report</a></p>
            <div class="summary">%s</div>
            <table>
              <thead>
                <tr>
                  <th>Result</th>
                  <th>Test</th>
                  <th>Suite</th>
                  <th>Duration</th>
                  <th>Failure details</th>
                  <th>Screenshot</th>
                </tr>
              </thead>
              <tbody>
                %s
              </tbody>
            </table>
        """, summaryHtml, renderLegacyRows(tests)));

        Files.writeString(SUREFIRE_REPORT_PATH, legacyHtml, StandardCharsets.UTF_8);
    }

    private static String renderLegacyRows(List<TestRecord> tests) {
        StringBuilder sb = new StringBuilder();
        for (TestRecord test : tests) {
            String detailLink = "../../custom-report/" + fileId(test.id) + ".html";
            String simpleSuite = test.className.contains(".")
                ? test.className.substring(test.className.lastIndexOf('.') + 1)
                : test.className;
            sb.append("<tr>");
            sb.append("<td><span class=\"badge ").append(test.status).append("\">").append(escapeHtml(test.status)).append("</span></td>");
            sb.append("<td><a href=\"").append(escapeHtml(detailLink)).append("\">").append(escapeHtml(test.displayName)).append("</a></td>");
            sb.append("<td>").append(escapeHtml(simpleSuite)).append("</td>");
            sb.append("<td>").append(test.durationMs).append(" ms</td>");
            sb.append("<td class=\"muted\">").append(test.failureDetails != null ? escapeHtml(firstLine(test.failureDetails)) : "").append("</td>");
            sb.append("<td>");
            if (test.screenshotFile != null) {
                sb.append("<a href=\"../screenshots/").append(escapeHtml(test.screenshotFile)).append("\" target=\"_blank\">View</a>");
            } else {
                sb.append("None");
            }
            sb.append("</td></tr>");
        }
        return sb.toString();
    }

    private static String firstLine(String text) {
        if (text == null) return "";
        int newline = text.indexOf('\n');
        return newline > 0 ? text.substring(0, newline) : text;
    }

    private static String renderTestDetails(TestRecord test) {
        String simpleClassName = test.className.contains(".")
            ? test.className.substring(test.className.lastIndexOf('.') + 1)
            : test.className;

        StringBuilder content = new StringBuilder();
        content.append("<p><a href=\"index.html\">← Back to test summary</a></p>");
        content.append("<h1>").append(escapeHtml(test.displayName)).append("</h1>");
        content.append("<p><span class=\"badge ").append(test.status).append("\">").append(escapeHtml(test.status)).append("</span></p>");
        content.append("<p class=\"muted\">").append(escapeHtml(test.className)).append(" › ").append(escapeHtml(test.methodName)).append("</p>");
        content.append("<p class=\"muted\">").append(escapeHtml(simpleClassName)).append(".java · Framework: Java Selenium / JUnit 5</p>");

        content.append("<section>");
        content.append("<h2>Execution Details <span class=\"badge ").append(test.status).append("\">").append(escapeHtml(test.status)).append("</span></h2>");
        content.append("<p class=\"muted\">Duration: ").append(test.durationMs).append(" ms</p>");

        if (test.failureDetails != null && !test.failureDetails.isBlank()) {
            content.append("<h3>Failure details</h3>");
            content.append("<pre class=\"error\">").append(escapeHtml(test.failureDetails)).append("</pre>");
        }

        content.append("<h3>Steps</h3>");
        content.append(renderSteps(test.steps));
        content.append(renderAttachments(test));
        content.append("</section>");

        return reportPage(test.displayName, content.toString());
    }

    private static String renderSteps(List<StepInfo> steps) {
        if (steps == null || steps.isEmpty()) {
            return "<p class=\"muted\">No detailed step data was recorded for this test.</p>";
        }
        StringBuilder sb = new StringBuilder("<ol class=\"steps\">");
        for (StepInfo step : steps) {
            String status = step.status;
            boolean isFailed = "failed".equals(status);
            sb.append("<li>");
            sb.append("<details ").append(isFailed ? "open" : "").append(">");
            sb.append("<summary>");
            sb.append("<span class=\"badge ").append(status).append("\">").append(escapeHtml(status)).append("</span>");
            sb.append("<span>").append(escapeHtml(step.title)).append("</span>");
            sb.append("<small>").append(step.durationMs).append(" ms</small>");
            sb.append("</summary>");
            if (step.error != null && !step.error.isBlank()) {
                sb.append("<pre class=\"error\">").append(escapeHtml(step.error)).append("</pre>");
            }
            sb.append("</details>");
            sb.append("</li>");
        }
        sb.append("</ol>");
        return sb.toString();
    }

    private static String firstFailedStep(TestRecord test) {
        for (StepInfo step : test.steps) {
            if ("failed".equals(step.status)) {
                return step.title;
            }
        }
        return "Test execution: " + test.methodName;
    }

    private static String renderAttachments(TestRecord test) {
        if (test.screenshotFile == null) {
            if (test.screenshotError != null) {
                return "<div class=\"artifact\"><p class=\"muted\">Screenshot status: " + escapeHtml(test.screenshotError) + "</p></div>";
            }
            return "";
        }
        String assetPath = "assets/" + test.screenshotFile;
        StringBuilder sb = new StringBuilder("<div class=\"artifact\">");
        if ("failed".equals(test.status)) {
            String failedStep = firstFailedStep(test);
            sb.append("<h3>Failure screenshot</h3>");
            sb.append("<p class=\"muted\">Captured after the test failed. Associated failed step: <strong>")
              .append(escapeHtml(failedStep)).append("</strong>.</p>");
            sb.append("<a href=\"").append(escapeHtml(assetPath)).append("\" target=\"_blank\">")
              .append("<img class=\"screenshot\" src=\"").append(escapeHtml(assetPath))
              .append("\" alt=\"Failure screenshot for ").append(escapeHtml(failedStep)).append("\"></a>");
        } else {
            sb.append("<h3>Test screenshot</h3>");
            sb.append("<p class=\"muted\">Captured at conclusion of test.</p>");
            sb.append("<a href=\"").append(escapeHtml(assetPath)).append("\" target=\"_blank\">")
              .append("<img class=\"screenshot\" src=\"").append(escapeHtml(assetPath))
              .append("\" alt=\"Screenshot for ").append(escapeHtml(test.displayName)).append("\"></a>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static String reportPage(String title, String content) {
        return String.format("""
            <!doctype html>
            <html lang="en">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>%s</title>
              <style>
            %s
              </style>
            </head>
            <body><main>%s</main></body>
            </html>
        """, escapeHtml(title), SHARED_CSS, content);
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    public static class StepInfo {
        final String title;
        volatile String status = "running";
        volatile long durationMs = 0;
        volatile String error;

        public StepInfo(String title) {
            this.title = title;
        }

        public void complete(String status, long durationMs, String error) {
            this.status = status;
            this.durationMs = durationMs;
            this.error = error;
        }
    }

    public static class TestRecord {
        final String id;
        final String className;
        final String methodName;
        final String displayName;
        volatile String status = "running";
        volatile long startTime = 0;
        volatile long durationMs = 0;
        volatile String failureDetails;
        volatile String screenshotFile;
        volatile String screenshotError;
        final List<StepInfo> steps = Collections.synchronizedList(new ArrayList<>());

        public TestRecord(String id, String className, String methodName, String displayName) {
            this.id = id;
            this.className = className;
            this.methodName = methodName;
            this.displayName = displayName;
        }

        public void addStep(StepInfo step) {
            steps.add(step);
        }

        public void updateStep(String title, String status, long durationMs, String error) {
            synchronized (steps) {
                for (int i = steps.size() - 1; i >= 0; i--) {
                    StepInfo s = steps.get(i);
                    if (s.title.equals(title)) {
                        if ("failed".equals(s.status) && !"failed".equals(status)) {
                            return;
                        }
                        s.complete(status, durationMs, error);
                        return;
                    }
                }
            }
            StepInfo s = new StepInfo(title);
            s.complete(status, durationMs, error);
            steps.add(s);
        }
    }
}

