import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Medicine } from '../../../core/models/medicine.model';

@Component({
  selector: 'app-medicine-dialog',
  templateUrl: './medicine-dialog.component.html',
  styles: [`
    .dialog-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
      margin-top: 10px;
      min-width: 300px;
    }
    .full-width {
      width: 100%;
    }
  `]
})
export class MedicineDialogComponent implements OnInit {
  medicineForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<MedicineDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Medicine
  ) {
    this.medicineForm = this.fb.group({
      medicineName: ['', Validators.required],
      dosage: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.data) {
      this.medicineForm.patchValue({
        medicineName: this.data.medicineName,
        dosage: this.data.dosage
      });
    }
  }

  save(): void {
    if (this.medicineForm.valid) {
      this.dialogRef.close(this.medicineForm.value);
    }
  }
}
