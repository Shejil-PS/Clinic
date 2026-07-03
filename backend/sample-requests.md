# Sample JSON Requests and Responses

## 1. Authentication Module

### Register a User
**POST** `/api/v1/auth/register`
```json
{
  "fullName": "Admin User",
  "username": "admin",
  "password": "password123",
  "email": "admin@clinic.com",
  "phone": "9876543210"
}
```
**Response** (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhd... (JWT)",
  "userId": "e6a0c0a9-2b0e-4340-9799-eb0c1b4b1a8d",
  "fullName": "Admin User",
  "username": "admin"
}
```

### Login
**POST** `/api/v1/auth/login`
```json
{
  "username": "admin",
  "password": "password123"
}
```
**Response** (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhd... (JWT)",
  "userId": "e6a0c0a9-2b0e-4340-9799-eb0c1b4b1a8d",
  "fullName": "Admin User",
  "username": "admin"
}
```

---

## 2. Patient Module

### Create a Patient
**POST** `/api/v1/patients`
*Header: `Authorization: Bearer <token>`*
```json
{
  "fullName": "John Doe",
  "gender": "Male",
  "dateOfBirth": "1990-05-15",
  "age": 34,
  "bloodGroup": "O+",
  "phone": "1234567890",
  "email": "john.doe@example.com",
  "address": "123 Main St",
  "occupation": "Software Engineer",
  "medicalHistory": "None",
  "allergies": "Peanuts",
  "remarks": "First visit"
}
```
**Response** (201 Created)
```json
{
  "id": "64abcdef...",
  "patientId": "PAT000001",
  "fullName": "John Doe",
  "gender": "Male",
  "dateOfBirth": "1990-05-15",
  "age": 34,
  "bloodGroup": "O+",
  "phone": "1234567890",
  "email": "john.doe@example.com",
  "address": "123 Main St",
  "occupation": "Software Engineer",
  "medicalHistory": "None",
  "allergies": "Peanuts",
  "remarks": "First visit",
  "active": true,
  "createdAt": "2023-11-20T10:00:00",
  "updatedAt": "2023-11-20T10:00:00"
}
```

---

## 3. Medicine Module

### Create a Medicine
**POST** `/api/v1/medicines`
*Header: `Authorization: Bearer <token>`*
```json
{
  "medicineName": "Paracetamol 500mg",
  "available": true
}
```
**Response** (201 Created)
```json
{
  "id": "64bcdef0...",
  "medicineId": "MED000001",
  "medicineName": "Paracetamol 500mg",
  "available": true,
  "createdAt": "2023-11-20T10:30:00",
  "updatedAt": "2023-11-20T10:30:00"
}
```

---

## 4. Visit Module

### Create a Visit
**POST** `/api/v1/visits`
*Header: `Authorization: Bearer <token>`*
```json
{
  "patientId": "PAT000001",
  "doctorName": "Dr. Smith",
  "chiefComplaint": "Fever and Headache",
  "diagnosis": "Viral Fever",
  "bloodPressure": "120/80",
  "pulseRate": 85,
  "temperature": 101.5,
  "height": 175.0,
  "weight": 70.5,
  "notes": "Advised rest for 3 days",
  "followUpDate": "2023-11-23"
}
```
**Response** (201 Created)
```json
{
  "id": "64cdef01...",
  "visitId": "VIS000001",
  "patientId": "PAT000001",
  "visitDate": "2023-11-20T11:00:00",
  "doctorName": "Dr. Smith",
  "chiefComplaint": "Fever and Headache",
  "diagnosis": "Viral Fever",
  "bloodPressure": "120/80",
  "pulseRate": 85,
  "temperature": 101.5,
  "height": 175.0,
  "weight": 70.5,
  "notes": "Advised rest for 3 days",
  "followUpDate": "2023-11-23",
  "createdAt": "2023-11-20T11:00:00",
  "updatedAt": "2023-11-20T11:00:00"
}
```

---

## 5. Prescription Module

### Create a Prescription
**POST** `/api/v1/prescriptions`
*Header: `Authorization: Bearer <token>`*
```json
{
  "patientId": "PAT000001",
  "visitId": "VIS000001",
  "remarks": "Take after meals",
  "medicineList": [
    {
      "medicineId": "MED000001",
      "medicineName": "Paracetamol 500mg",
      "dosage": "1-0-1",
      "duration": "3 days",
      "quantity": 6,
      "instruction": "After food",
      "dispensedFromClinic": true
    }
  ]
}
```
**Response** (201 Created)
```json
{
  "id": "64def012...",
  "prescriptionId": "PRE000001",
  "patientId": "PAT000001",
  "visitId": "VIS000001",
  "prescriptionDate": "2023-11-20T11:15:00",
  "remarks": "Take after meals",
  "medicineList": [
    {
      "medicineId": "MED000001",
      "medicineName": "Paracetamol 500mg",
      "dosage": "1-0-1",
      "duration": "3 days",
      "quantity": 6,
      "instruction": "After food",
      "dispensedFromClinic": true
    }
  ],
  "createdAt": "2023-11-20T11:15:00"
}
```

### Download PDF
**GET** `/api/v1/prescriptions/{id}/print`
*Header: `Authorization: Bearer <token>`*
**Response:** PDF File Stream
