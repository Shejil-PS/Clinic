@echo off
echo Starting Clinic Management Backend...
echo This bypasses IntelliJ's broken compiler cache.
set SPRING_DATA_MONGODB_URI=mongodb+srv://ShejilPS:Clinic@cluster0.huwwbd2.mongodb.net/clinic_management?retryWrites=true^&w=majority^&appName=Cluster0^&compressors=zlib
call mvn clean compile spring-boot:run -DskipTests
pause
