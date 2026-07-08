import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `http://${window.location.hostname}:8080/api/v1/auth`;
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
        // Handle ApiResponse wrapper if present
        const token = response.data ? response.data.token : response.token;
        if (token) {
          localStorage.setItem('token', token);
          this.currentTokenSubject.next(token);
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
