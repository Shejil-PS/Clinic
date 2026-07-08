# Dental Clinic Management System - Migration & Server Guide

This document outlines the steps to migrate the Clinic Management System to a new server and optionally configure it to run as a background Windows Service using WinSW.

---

## 1. Prerequisites on the New Server
- **Java 21:** Download and install the Java 21 JDK or JRE. Ensure the `java` command is available in the system's `PATH`.
- **MongoDB:** Download and install MongoDB Community Server. Ensure the MongoDB service is running (default port is `27017`).

---

## 2. Transfer the Application
1. Copy the entire `ClinicServer` folder from your development machine to the new server (e.g., to `C:\ClinicServer`).
2. The folder contains the executable JAR, scripts, and configuration properties.

---

## 3. Data Migration (Optional)
To copy your existing user accounts, patients, and clinic data:
1. On your **current** machine, open a terminal and export the database:
   ```cmd
   mongodump --db clinic_management --out C:\backup
   ```
2. Copy the `C:\backup\clinic_management` folder to the new server.
3. On the **new** server, import the database:
   ```cmd
   mongorestore --db clinic_management path\to\backup\clinic_management
   ```

---

## 4. Configuration Adjustments
Open `ClinicServer\config\application-prod.properties` on the new server:
- **`server.port`**: Change this if port `8080` is already in use.
- **`spring.data.mongodb.uri`**: If MongoDB requires authentication, update the URI (e.g., `mongodb://user:pass@localhost:27017/clinic_management`).

---

## 5. Network / Firewall Configuration
To allow other computers on the network to access the application:
1. Open **Windows Defender Firewall with Advanced Security**.
2. Create a new **Inbound Rule** -> **Port** -> **TCP**.
3. Specify the port used in your properties file (e.g., `8080`).
4. Select **Allow the connection**.
5. Name it "Clinic Management System" and save.

You can now access the system from another computer using `http://<NEW_SERVER_IP>:8080`.

---

## 6. Running as a Windows Service (Using WinSW)

Running the application as a Windows Service ensures it starts automatically when the server boots up, runs in the background, and restarts automatically if it crashes.

### Steps to configure:
1. **Download WinSW:**
   - Go to the [WinSW GitHub Releases page](https://github.com/winsw/winsw/releases) and download the latest executable (e.g., `WinSW-x64.exe`).

2. **Setup the Executable:**
   - Place the downloaded `.exe` file directly into the `ClinicServer` directory.
   - Rename it to **`clinic-service.exe`**.

3. **Verify Configuration:**
   - A configuration file named `clinic-service.xml` is already provided in the `ClinicServer` folder.
   - Ensure it is in the exact same folder as `clinic-service.exe`.

4. **Install the Service:**
   - Open Command Prompt **as Administrator**.
   - Navigate to the `ClinicServer` folder:
     ```cmd
     cd C:\path\to\ClinicServer
     ```
   - Run the install command:
     ```cmd
     clinic-service.exe install
     ```

5. **Start the Service:**
   - Run:
     ```cmd
     clinic-service.exe start
     ```
   - The application is now running as a background service!

### Service Management Commands (Run as Administrator):
- **Stop Service:** `clinic-service.exe stop`
- **Restart Service:** `clinic-service.exe restart`
- **Uninstall Service:** `clinic-service.exe uninstall` (Make sure to stop it first)
- **Check Status:** `clinic-service.exe status`
