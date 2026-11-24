@echo off
setlocal
echo ====================================================
echo Dental Clinic Management - Database Backup Utility
echo ====================================================

REM Get current timestamp in YYYY-MM-DD_HH-MM format
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set TIMESTAMP=%datetime:~0,4%-%datetime:~4,2%-%datetime:~6,2%_%datetime:~8,2%-%datetime:~10,2%

set BACKUP_DIR=%~dp0..\backup\%TIMESTAMP%
mkdir "%BACKUP_DIR%" 2>nul

echo Creating backup of 'clinic_management' database...
mongodump --db clinic_management --out "%BACKUP_DIR%"

if %ERRORLEVEL% equ 0 (
    echo.
    echo [SUCCESS] Backup completed successfully!
    echo Saved to: %BACKUP_DIR%
) else (
    echo.
    echo [WARNING] mongodump command failed or mongodump is not in system PATH.
    echo Please ensure MongoDB Database Tools are installed.
)

echo ====================================================
pause

