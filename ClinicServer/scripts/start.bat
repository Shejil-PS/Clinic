@echo off
echo Starting Dental Clinic Management System...
title ClinicManagementSystem
cd %~dp0..
java -jar clinic-management.jar --spring.config.location=file:config/application-prod.properties
pause
