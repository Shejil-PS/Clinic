import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Patient, Visit } from '../models/patient.model';

@Injectable({
  providedIn: 'root'
})
export class ConsultationService {
  private baseUrl = `http://${window.location.hostname}:8080/api/v1`;
  private patientUrl = `${this.baseUrl}/patients`;
  private visitUrl = `${this.baseUrl}/visits`;

  constructor(private http: HttpClient) { }

  searchPatientByPhone(phone: string): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.patientUrl}/phone/${phone}`);
  }

  registerPatient(patient: Patient): Observable<Patient> {
    return this.http.post<Patient>(this.patientUrl, patient);
  }

  getPatientProfile(patientId: string): Observable<any> {
    return this.http.get<any>(`${this.patientUrl}/profile/${patientId}`);
  }

  getAllPatients(): Observable<Patient[]> {
    let params = new HttpParams().set('size', '100000');
    return this.http.get<any>(this.patientUrl, { params }).pipe(
      map(response => response.content || response)
    );
  }

  registerVisit(patientId: string): Observable<Visit> {
    return this.http.post<Visit>(this.visitUrl, { patientId });
  }

  getVisitsByDate(dateStr: string): Observable<any[]> {
    let params = new HttpParams().set('date', dateStr);
    return this.http.get<any>(`${this.visitUrl}/queue`, { params }).pipe(
      map(response => response.content || response)
    );
  }

  getVisitById(visitId: string): Observable<Visit> {
    return this.http.get<Visit>(`${this.visitUrl}/${visitId}`);
  }

  finishConsultation(visitId: string, payload: any): Observable<Visit> {
    return this.http.post<Visit>(`${this.baseUrl}/visits/${visitId}/finish`, payload);
  }

  uploadFile(file: File): Observable<{fileUrl: string}> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{fileUrl: string}>(`${this.baseUrl}/files/upload`, formData);
  }

  getBillByVisitId(visitId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/bills/visit/${visitId}`);
  }

  updateBill(billId: string, payload: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/bills/${billId}`, payload);
  }

  getPrescriptionByVisitId(visitId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/prescriptions/visit/${visitId}`);
  }

  addPrescription(prescription: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/prescriptions`, prescription);
  }

  addTreatment(treatment: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/treatments`, treatment);
  }
}
