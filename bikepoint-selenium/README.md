# TfL BikePoint Selenium API test

This Maven project uses Selenium Chrome to open the TfL Swagger UI and make same-origin API requests from the browser. It checks:

- `GET /BikePoint` returns HTTP 200, valid location fields, and non-negative `NbBikes`, `NbDocks`, and `NbEmptyDocks`/`NbSpaces` values.
- `GET /BikePoint/Search?query=` returns HTTP 400 with a required-query message.
- An unknown `GET /BikePoint/{id}` returns HTTP 404.

## Build and run

From this directory, build the executable JAR with:

```powershell
mvn -DskipTests package
```

Run the built JAR with:

```powershell
java -jar target/bikepoint-selenium-test.jar
```

Or run `run-tests.bat`, which builds the JAR if it is missing and then executes it. The batch script locates Java through `JAVA_HOME`, common JDK install locations, or `PATH`.

Requirements: Maven, a JDK 17 or newer, Google Chrome, and network access to `api.tfl.gov.uk`. Selenium Manager may download a matching ChromeDriver on first run.
