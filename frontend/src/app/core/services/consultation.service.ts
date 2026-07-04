import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Patient, Visit } from '../models/patient.model';

@Injectable({
  providedIn: 'root'
})
export class ConsultationService {
  private patientUrl = 'http://localhost:8080/api/v1/patients';
  private visitUrl = 'http://localhost:8080/api/v1/visits';

  constructor(private http: HttpClient) { }

  searchPatientByPhone(phone: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.patientUrl}/phone/${phone}`);
  }

  registerPatient(patient: Patient): Observable<Patient> {
    return this.http.post<Patient>(this.patientUrl, patient);
  }

  registerVisit(patientId: string): Observable<Visit> {
    return this.http.post<Visit>(this.visitUrl, { patientId });
  }
}
