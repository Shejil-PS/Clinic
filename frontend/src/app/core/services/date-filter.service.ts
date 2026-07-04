import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DateFilterService {
  private selectedDateSubject = new BehaviorSubject<string>(new Date().toISOString().split('T')[0]);
  
  // Observable for components to subscribe to
  selectedDate$ = this.selectedDateSubject.asObservable();

  constructor() { }

  setDate(dateStr: string) {
    this.selectedDateSubject.next(dateStr);
  }

  getCurrentDate(): string {
    return this.selectedDateSubject.value;
  }
}
