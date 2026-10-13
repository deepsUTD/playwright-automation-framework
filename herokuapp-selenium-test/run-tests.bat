@echo off
setlocal
cd /d "%~dp0"
where mvn.cmd >nul 2>nul
if errorlevel 1 (
  echo Maven was not found. Install Maven and add mvn.cmd to PATH.
  exit /b 1
)
call mvn.cmd test %*
exit /b %ERRORLEVEL%
