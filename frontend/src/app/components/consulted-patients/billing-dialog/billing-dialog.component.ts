import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { VisitDTO } from '../../../core/models/patient.model';

@Component({
  selector: 'app-billing-dialog',
  templateUrl: './billing-dialog.component.html',
  styles: [`
    .patient-info {
      background: #f8f9fa;
      padding: 12px;
      border-radius: 4px;
      margin-bottom: 20px;
      p { margin: 4px 0; color: #2c3e50; }
    }
    .bill-form { margin-top: 10px; }
    .full-width { width: 100%; }
    .total-section {
      text-align: right;
      padding-top: 16px;
      border-top: 1px dashed #ccc;
      h3 { margin: 0; color: #27ae60; font-size: 24px; }
    }
  `]
})
export class BillingDialogComponent implements OnInit {
  billForm: FormGroup;
  totalAmount: number = 0;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<BillingDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VisitDTO
  ) {
    this.billForm = this.fb.group({
      consultationFee: [250, [Validators.required, Validators.min(0)]],
      treatmentCharges: [0, [Validators.required, Validators.min(0)]],
      medicineCharges: [0, [Validators.required, Validators.min(0)]],
      discount: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.calculateTotal();
  }

  calculateTotal() {
    const vals = this.billForm.value;
    const cons = vals.consultationFee || 0;
    const treat = vals.treatmentCharges || 0;
    const med = vals.medicineCharges || 0;
    const disc = vals.discount || 0;
    
    this.totalAmount = (cons + treat + med) - disc;
    if (this.totalAmount < 0) this.totalAmount = 0;
  }

  saveBill() {
    if (this.billForm.valid) {
      // Typically we would save this to the DB.
      // After saving, trigger print.
      window.print();
      this.dialogRef.close(this.billForm.value);
    }
  }
}
