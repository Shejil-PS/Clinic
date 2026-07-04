import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MedicineService } from '../../core/services/medicine.service';
import { Medicine } from '../../core/models/medicine.model';
import { MedicineDialogComponent } from './medicine-dialog/medicine-dialog.component';

@Component({
  selector: 'app-medicine-master',
  templateUrl: './medicine-master.component.html',
  styleUrls: ['./medicine-master.component.scss']
})
export class MedicineMasterComponent implements OnInit {
  displayedColumns: string[] = ['medicineId', 'medicineName', 'dosage', 'actions'];
  dataSource!: MatTableDataSource<Medicine>;
  isLoading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private medicineService: MedicineService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadMedicines();
  }

  loadMedicines() {
    this.isLoading = true;
    this.medicineService.getAllMedicines(0, 1000).subscribe({
      next: (response) => {
        this.dataSource = new MatTableDataSource(response.content);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.showError('Failed to load medicines');
        this.isLoading = false;
      }
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  openDialog(medicine?: Medicine): void {
    const dialogRef = this.dialog.open(MedicineDialogComponent, {
      width: '400px',
      data: medicine
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (medicine && medicine.id) {
          // Edit
          this.medicineService.updateMedicine(medicine.id, result).subscribe({
            next: () => {
              this.showSuccess('Medicine updated successfully');
              this.loadMedicines();
            },
            error: () => this.showError('Failed to update medicine')
          });
        } else {
          // Create
          this.medicineService.createMedicine(result).subscribe({
            next: () => {
              this.showSuccess('Medicine created successfully');
              this.loadMedicines();
            },
            error: () => this.showError('Failed to create medicine')
          });
        }
      }
    });
  }

  deleteMedicine(medicine: Medicine): void {
    if (confirm(`Are you sure you want to delete ${medicine.medicineName}?`)) {
      this.medicineService.deleteMedicine(medicine.id!).subscribe({
        next: () => {
          this.showSuccess('Medicine deleted successfully');
          this.loadMedicines();
        },
        error: () => this.showError('Failed to delete medicine')
      });
    }
  }

  private showSuccess(message: string) {
    this.snackBar.open(message, 'Close', { duration: 3000, panelClass: 'success-snackbar' });
  }

  private showError(message: string) {
    this.snackBar.open(message, 'Close', { duration: 3000, panelClass: 'error-snackbar' });
  }
}
