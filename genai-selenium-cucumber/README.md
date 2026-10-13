# GenAI Selenium Cucumber

A standalone Java 17+, Maven, Selenium, and Cucumber project showing how generated synthetic data can drive an end-to-end UI test. The sample enters generated names into the Rahul Shetty AutomationPractice alert flow and checks the exact browser response.

Open `genai-selenium-cucumber.code-workspace` to work with this project independently from the other automation suites.

## Run without AI credentials

Requirements: JDK 17+, Maven, Chrome, and network access to the practice site. Selenium Manager may download a matching ChromeDriver on first run.

From this directory:

```powershell
mvn -DskipTests package
java -jar target/genai-selenium-cucumber.jar
```

Or run `run-tests.bat`; it builds the shaded JAR and returns the test exit code. If `GENAI_API_KEY` is unset, deterministic synthetic fixtures are used so the UI flow can run without a provider account.

## Configure an AI provider

The adapter uses the common chat-completions request/response shape and is configured only through environment variables:

- `GENAI_API_KEY`: credential for the compatible provider
- `GENAI_BASE_URL`: API root, default `https://api.openai.com/v1`
- `GENAI_MODEL`: model name, default `gpt-4o-mini`

PowerShell example:

```powershell
$env:GENAI_API_KEY = "your-key"
$env:GENAI_BASE_URL = "https://api.openai.com/v1"
$env:GENAI_MODEL = "gpt-4o-mini"
cmd /c run-tests.bat
```

Do not commit credentials. The API key is sent only as an authorization header and is never logged. The `AiTestDataProvider` interface can be implemented for another provider or an internal test-data service.

## Scenarios

- Provider-generated synthetic names round-trip through the alert and clear the form field.
- Empty-name alert behavior.
- Names containing punctuation survive the alert round trip.
