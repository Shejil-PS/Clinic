import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ConsultationService } from '../../../core/services/consultation.service';
import { VisitDTO } from '../../../core/models/patient.model';

@Component({
  selector: 'app-history-dialog',
  templateUrl: './history-dialog.component.html',
  styleUrls: ['./history-dialog.component.scss']
})
export class HistoryDialogComponent implements OnInit {
  isLoading = true;
  profileData: any = null;
  flattenedMedicines: any[] = [];

  constructor(
    public dialogRef: MatDialogRef<HistoryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VisitDTO,
    private consultationService: ConsultationService
  ) {}

  ngOnInit(): void {
    this.consultationService.getPatientProfile(this.data.patientId).subscribe({
      next: (res) => {
        this.profileData = res;
        this.flattenMedicines();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load profile', err);
        this.isLoading = false;
      }
    });
  }

  flattenMedicines() {
    this.flattenedMedicines = [];
    if (this.profileData && this.profileData.prescriptionHistory) {
      this.profileData.prescriptionHistory.forEach((prescription: any) => {
        if (prescription.medicineList && Array.isArray(prescription.medicineList)) {
          prescription.medicineList.forEach((med: any) => {
            this.flattenedMedicines.push({
              date: prescription.createdAt || prescription.prescriptionDate,
              medicineName: med.medicineName,
              instruction: med.instruction
            });
          });
        }
      });
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
