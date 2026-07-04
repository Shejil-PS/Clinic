import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConsultationService } from '../../core/services/consultation.service';
import { VisitDTO } from '../../core/models/patient.model';

@Component({
  selector: 'app-consultation-queue',
  templateUrl: './consultation-queue.component.html',
  styleUrls: ['./consultation-queue.component.scss']
})
export class ConsultationQueueComponent implements OnInit {
  displayedColumns: string[] = ['patientId', 'patientName', 'status', 'actions'];
  dataSource!: MatTableDataSource<VisitDTO>;
  isLoading = false;
  
  selectedDate: string;

  constructor(
    private consultationService: ConsultationService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    // Default to today
    const today = new Date();
    this.selectedDate = today.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.loadQueue();
  }

  onDateChange(event: any) {
    this.selectedDate = event.target.value;
    this.loadQueue();
  }

  loadQueue() {
    if (!this.selectedDate) return;
    
    this.isLoading = true;
    this.consultationService.getVisitsByDate(this.selectedDate).subscribe({
      next: (visits) => {
        this.dataSource = new MatTableDataSource(visits);
        this.isLoading = false;
      },
      error: () => {
        this.snackBar.open('Failed to load consultation queue', 'Close', { duration: 3000, panelClass: 'error-snackbar' });
        this.isLoading = false;
      }
    });
  }

  startConsultation(visitId: string) {
    this.router.navigate(['/consultation', visitId]);
  }
}
