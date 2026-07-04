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

  getVisitsByDate(dateStr: string): Observable<any[]> {
    let params = new HttpParams().set('date', dateStr);
    return this.http.get<any[]>(`${this.visitUrl}/date`, { params });
  }

  getVisitById(visitId: string): Observable<Visit> {
    return this.http.get<Visit>(`${this.visitUrl}/${visitId}`);
  }

  finishConsultation(visitId: string): Observable<any> {
    return this.http.post(`${this.visitUrl}/${visitId}/finish`, {});
  }

  addPrescription(prescription: any): Observable<any> {
    return this.http.post('http://localhost:8080/api/v1/prescriptions', prescription);
  }

  addTreatment(treatment: any): Observable<any> {
    return this.http.post('http://localhost:8080/api/v1/treatments', treatment);
  }
}
