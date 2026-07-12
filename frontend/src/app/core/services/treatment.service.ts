import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TreatmentService {
  private apiUrl = `http://${window.location.hostname}:8080/api/v1/treatments/master`;

  constructor(private http: HttpClient) { }

  getAllTreatments(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }
}
