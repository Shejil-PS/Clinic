import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { ConsultationService } from '../../core/services/consultation.service';
import { DateFilterService } from '../../core/services/date-filter.service';
import { VisitDTO } from '../../core/models/patient.model';
import { BillingDialogComponent } from './billing-dialog/billing-dialog.component';

@Component({
  selector: 'app-consulted-patients',
  templateUrl: './consulted-patients.component.html',
  styleUrls: ['./consulted-patients.component.scss']
})
export class ConsultedPatientsComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['patientId', 'patientName', 'status', 'actions'];
  dataSource!: MatTableDataSource<VisitDTO>;
  isLoading = false;
  
  selectedDate: string = '';
  private dateSub!: Subscription;

  constructor(
    private consultationService: ConsultationService,
    private dateFilterService: DateFilterService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.dateSub = this.dateFilterService.selectedDate$.subscribe(date => {
      this.selectedDate = date;
      this.loadConsultedVisits();
    });
  }

  ngOnDestroy(): void {
    if (this.dateSub) {
      this.dateSub.unsubscribe();
    }
  }

  loadConsultedVisits() {
    if (!this.selectedDate) return;
    
    this.isLoading = true;
    this.consultationService.getVisitsByDate(this.selectedDate).subscribe({
      next: (visits) => {
        // Filter only CONSULTED visits
        const consulted = visits.filter(v => v.visitStatus === 'CONSULTED');
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

  exportToCsv() {
    if (!this.dataSource || this.dataSource.data.length === 0) {
      return;
    }

    const data = this.dataSource.data;
    const csvRows = [];
    
    // Headers
    csvRows.push(['Patient ID', 'Patient Name', 'Visit ID', 'Status', 'Date'].join(','));
    
    // Rows
    for (const row of data) {
      const values = [
        row.patientId,
        `"${row.patientName}"`,
        row.visitId,
        row.visitStatus || row.status,
        this.selectedDate
      ];
      csvRows.push(values.join(','));
    }
    
    const csvString = csvRows.join('\n');
    const blob = new Blob([csvString], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    
    const a = document.createElement('a');
    a.setAttribute('hidden', '');
    a.setAttribute('href', url);
    a.setAttribute('download', `consulted_patients_${this.selectedDate}.csv`);
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }
}
