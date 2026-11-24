@echo off
echo Starting Clinic Management Backend...
if "%SPRING_DATA_MONGODB_URI%"=="" (
    set SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/clinic_management
)
call mvn clean compile spring-boot:run -DskipTests
pause
