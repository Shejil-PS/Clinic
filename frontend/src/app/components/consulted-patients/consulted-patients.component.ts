import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConsultationService } from '../../core/services/consultation.service';
import { VisitDTO } from '../../core/models/patient.model';
import { BillingDialogComponent } from './billing-dialog/billing-dialog.component';

@Component({
  selector: 'app-consulted-patients',
  templateUrl: './consulted-patients.component.html',
  styleUrls: ['./consulted-patients.component.scss']
})
export class ConsultedPatientsComponent implements OnInit {
  displayedColumns: string[] = ['patientId', 'patientName', 'status', 'actions'];
  dataSource!: MatTableDataSource<VisitDTO>;
  isLoading = false;
  
  selectedDate: string;

  constructor(
    private consultationService: ConsultationService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {
    const today = new Date();
    this.selectedDate = today.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.loadConsultedVisits();
  }

  onDateChange(event: any) {
    this.selectedDate = event.target.value;
    this.loadConsultedVisits();
  }

  loadConsultedVisits() {
    if (!this.selectedDate) return;
    
    this.isLoading = true;
    this.consultationService.getVisitsByDate(this.selectedDate).subscribe({
      next: (visits) => {
        // Filter only CONSULTED visits
        const consulted = visits.filter(v => v.status === 'CONSULTED');
        this.dataSource = new MatTableDataSource(consulted);
        this.isLoading = false;
      },
      error: () => {
        this.snackBar.open('Failed to load consulted patients', 'Close', { duration: 3000, panelClass: 'error-snackbar' });
        this.isLoading = false;
      }
    });
  }

  openBillingDialog(visit: VisitDTO) {
    const dialogRef = this.dialog.open(BillingDialogComponent, {
      width: '600px',
      data: visit
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.snackBar.open('Bill Generated Successfully!', 'Close', { duration: 3000, panelClass: 'success-snackbar' });
      }
    });
  }

  printPrescription(visit: VisitDTO) {
    // In a real app, this would open a PDF or print window.
    // For this prototype, we'll just show a success message or trigger browser print.
    this.snackBar.open(`Generating PDF Prescription for ${visit.patientName}...`, 'Close', { duration: 3000 });
    window.print();
  }
}
