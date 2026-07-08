# Dental Clinic Management System - Deployment Guide

This directory contains the production-ready deployment package for the Dental Clinic Management System.

## Architecture
This is a self-contained Spring Boot executable JAR that also serves the compiled Angular frontend. It requires only Java (21) and MongoDB to be installed on the host server.

## Directory Structure
- `clinic-management.jar`: The main executable Spring Boot application.
- `config/application-prod.properties`: Externalized configuration.
- `logs/`: Directory where application logs are stored.
- `scripts/`: Batch scripts to start, stop, and restart the application.

---

## 1. How to Start / Stop / Restart

**Start:**
Run `scripts\start.bat`. This will start the application in the foreground using the external configuration.

**Stop:**
Run `scripts\stop.bat`. This will safely terminate the running java process.

**Restart:**
Run `scripts\restart.bat`. This stops the process and starts it again.

---

## 2. Configuration Settings
All settings are located in `config/application-prod.properties`.

- **MongoDB Connection:** Edit `spring.data.mongodb.uri`.
- **Server Port:** Edit `server.port` (default is 8080).
- **JWT Secret:** Edit `jwt.secret`. Ensure this is a secure, base64 encoded string.

---

## 3. How to Update the Application in the Future

### Updating the Frontend (Angular)
1. Navigate to the `frontend` directory in the source code.
2. Run `ng build --configuration production`.
3. Copy the contents of `frontend/dist/frontend/` to `backend/src/main/resources/static/`.
4. Rebuild the backend (see below).

### Updating the Backend (Spring Boot)
1. Navigate to the `backend` directory in the source code.
2. Run `mvn clean package -DskipTests`.
3. The new JAR will be generated in `backend/target/clinic-management-0.0.1-SNAPSHOT.jar` (or similar).

### Replacing the JAR
1. Run `scripts\stop.bat` to stop the current running instance.
2. Replace `clinic-management.jar` in this folder with the newly generated JAR from `backend/target/`.
3. Run `scripts\start.bat` to bring the application back online.

---

## 4. Troubleshooting

- **Application won't start?** Check the `logs/` directory for errors. Ensure MongoDB is running and accessible at the URI specified in `application-prod.properties`.
- **UI shows 404 on refresh?** The Spring Boot backend includes a `ForwardingController` to route all unmatched requests to `index.html`. Ensure the frontend was correctly copied to `static/` before packaging.
- **Port already in use?** Change the `server.port` in `config/application-prod.properties` or kill the process using the port.
