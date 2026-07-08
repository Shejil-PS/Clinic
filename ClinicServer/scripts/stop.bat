@echo off
echo Stopping Dental Clinic Management System...
REM Find the process running clinic-management.jar and kill it
for /f "tokens=1" %%i in ('jps -l ^| findstr "clinic-management.jar"') do (
    echo Killing process %%i
    taskkill /F /PID %%i
)
echo Application stopped.
pause
