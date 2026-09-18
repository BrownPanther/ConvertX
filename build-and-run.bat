@echo off
setlocal

echo ========================================
echo ConvertX - Build and Run
echo ========================================

echo.
echo [1/2] Running tests...
call mvn clean test
if errorlevel 1 (
    echo.
    echo Tests failed. Build stopped.
    pause
    exit /b 1
)

echo.
echo [2/2] Packaging application...
call mvn clean package
if errorlevel 1 (
    echo.
    echo Packaging failed.
    pause
    exit /b 1
)

echo.
echo Build successful.
echo Starting ConvertX...
echo.
java -jar target\convertx-1.0.0.jar

endlocal
