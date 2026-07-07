import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://10.88.155.227:8080/api/v1/auth';
  private currentTokenSubject: BehaviorSubject<string | null>;

  constructor(private http: HttpClient, private router: Router) {
    this.currentTokenSubject = new BehaviorSubject<string | null>(localStorage.getItem('token'));
  }

  public get currentToken(): string | null {
    return this.currentTokenSubject.value;
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials)
      .pipe(tap(response => {
        if (response && response.token) {
          localStorage.setItem('token', response.token);
          this.currentTokenSubject.next(response.token);
        }
      }));
  }

  logout() {
    localStorage.removeItem('token');
    this.currentTokenSubject.next(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!this.currentToken;
  }
}
