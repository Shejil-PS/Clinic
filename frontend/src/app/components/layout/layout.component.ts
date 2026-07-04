import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';
import { DateFilterService } from '../../core/services/date-filter.service';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnInit {
  selectedDate: string = '';
  
  constructor(
    private authService: AuthService,
    private dateFilterService: DateFilterService
  ) {}

  ngOnInit(): void {
    this.dateFilterService.selectedDate$.subscribe(date => {
      this.selectedDate = date;
    });
  }

  onDateChange(event: any) {
    this.dateFilterService.setDate(event.target.value);
  }

  logout() {
    this.authService.logout();
  }
}
