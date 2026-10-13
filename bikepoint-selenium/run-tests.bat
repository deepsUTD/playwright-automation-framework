@echo off
setlocal
cd /d "%~dp0"

set "JAR_PATH=%~dp0target\bikepoint-selenium-test.jar"
set "JAVA_EXE="

if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if not defined JAVA_EXE for /d %%D in ("%ProgramFiles%\Java\jdk-*") do if exist "%%~fD\bin\java.exe" set "JAVA_EXE=%%~fD\bin\java.exe"
if not defined JAVA_EXE for /f "delims=" %%J in ('where java.exe 2^>nul') do set "JAVA_EXE=%%J"

if not defined JAVA_EXE (
  echo Java was not found. Set JAVA_HOME or add java.exe to PATH.
  exit /b 1
)

if not exist "%JAR_PATH%" (
  echo Building the Selenium test JAR...
  call mvn.cmd -q -DskipTests package
  if errorlevel 1 exit /b %ERRORLEVEL%
)

"%JAVA_EXE%" -jar "%JAR_PATH%"
exit /b %ERRORLEVEL%
