@echo off
setlocal
cd /d "%~dp0"

where java.exe >nul 2>nul
if errorlevel 1 (
  echo Java was not found. Install JDK 17+ and add java.exe to PATH or set JAVA_HOME.
  exit /b 1
)

where mvn.cmd >nul 2>nul
if errorlevel 1 (
  echo Maven was not found. Install Maven and add mvn.cmd to PATH.
  exit /b 1
)

call mvn.cmd -q -DskipTests package
if errorlevel 1 exit /b %ERRORLEVEL%

java -jar "%~dp0target\genai-selenium-cucumber.jar"
exit /b %ERRORLEVEL%
