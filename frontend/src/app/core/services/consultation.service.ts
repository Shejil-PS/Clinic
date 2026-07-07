import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Patient, Visit } from '../models/patient.model';

@Injectable({
  providedIn: 'root'
})
export class ConsultationService {
  private patientUrl = 'http://10.88.155.227:8080/api/v1/patients';
  private visitUrl = 'http://10.88.155.227:8080/api/v1/visits';

  constructor(private http: HttpClient) { }

  searchPatientByPhone(phone: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.patientUrl}/phone/${phone}`);
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
    return this.http.post<Visit>(`http://10.88.155.227:8080/api/v1/visits/${visitId}/finish`, payload);
  }

  uploadFile(file: File): Observable<{fileUrl: string}> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{fileUrl: string}>('http://10.88.155.227:8080/api/v1/files/upload', formData);
  }

  getBillByVisitId(visitId: string): Observable<any> {
    return this.http.get(`http://10.88.155.227:8080/api/v1/bills/visit/${visitId}`);
  }

  getPrescriptionByVisitId(visitId: string): Observable<any> {
    return this.http.get(`http://10.88.155.227:8080/api/v1/prescriptions/visit/${visitId}`);
  }

  addPrescription(prescription: any): Observable<any> {
    return this.http.post('http://10.88.155.227:8080/api/v1/prescriptions', prescription);
  }

  addTreatment(treatment: any): Observable<any> {
    return this.http.post('http://10.88.155.227:8080/api/v1/treatments', treatment);
  }
}
