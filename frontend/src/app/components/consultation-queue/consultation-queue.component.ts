import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { ConsultationService } from '../../core/services/consultation.service';
import { DateFilterService } from '../../core/services/date-filter.service';
import { VisitDTO } from '../../core/models/patient.model';
import { HistoryDialogComponent } from './history-dialog/history-dialog.component';

@Component({
  selector: 'app-consultation-queue',
  templateUrl: './consultation-queue.component.html',
  styleUrls: ['./consultation-queue.component.scss']
})
export class ConsultationQueueComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['patientId', 'patientName', 'status', 'actions'];
  dataSource!: MatTableDataSource<VisitDTO>;
  isLoading = false;
  
  selectedDate: string = '';
  private dateSub!: Subscription;

  constructor(
    private consultationService: ConsultationService,
    private dateFilterService: DateFilterService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.dateSub = this.dateFilterService.selectedDate$.subscribe(date => {
      this.selectedDate = date;
      this.loadQueue();
    });
  }

  ngOnDestroy(): void {
    if (this.dateSub) {
      this.dateSub.unsubscribe();
    }
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

  viewHistory(visit: VisitDTO) {
    this.dialog.open(HistoryDialogComponent, {
      width: '700px',
      data: visit
    });
  }
}
