import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { VisitDTO } from '../../../core/models/patient.model';
import { ConsultationService } from '../../../core/services/consultation.service';

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
    
    #print-section {
      display: none !important;
    }
  `]
})
export class BillingDialogComponent implements OnInit {
  billForm: FormGroup;
  totalAmount: number = 0;
  totalTreatmentCost: number = 0;
  treatmentDetails: any[] = [];
  medicineDetails: any[] = [];
  currentDate = new Date();

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<BillingDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VisitDTO,
    private consultationService: ConsultationService
  ) {
    this.billForm = this.fb.group({
      printType: ['both'],
      consultationFee: [250, [Validators.required, Validators.min(0)]],
      medicineCharges: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.fetchBillDetails();
    this.calculateTotal();
    
    // Recalculate when form changes
    this.billForm.valueChanges.subscribe(() => {
      this.calculateTotal();
    });
  }

  billId: string = '';

  fetchBillDetails() {
    this.consultationService.getBillByVisitId(this.data.visitId).subscribe({
      next: (bill) => {
        if (bill) {
          this.billId = bill.id;
          if (bill.treatmentDetails) {
            this.treatmentDetails = bill.treatmentDetails;
            this.totalTreatmentCost = bill.treatmentDetails.reduce((sum: number, t: any) => sum + (t.cost || 0), 0);
          }
          this.calculateTotal();
        }
      },
      error: (err) => console.error('Failed to fetch bill details', err)
    });

    this.consultationService.getPrescriptionByVisitId(this.data.visitId).subscribe({
      next: (prescription) => {
        if (prescription && prescription.medicineList) {
          this.medicineDetails = prescription.medicineList;
        }
      },
      error: (err) => console.error('Failed to fetch prescription details', err)
    });
  }

  calculateTotal() {
    const vals = this.billForm.value;
    const cons = vals.consultationFee || 0;
    const med = vals.medicineCharges || 0;
    
    this.totalTreatmentCost = this.treatmentDetails.reduce((sum: number, t: any) => sum + (t.cost || 0), 0);
    
    this.totalAmount = cons + this.totalTreatmentCost + med;
    if (this.totalAmount < 0) this.totalAmount = 0;
  }

  saveBill() {
    if (this.billForm.get('printType')?.value === 'medicinesOnly') {
      this.printBill();
      this.dialogRef.close();
      return;
    }

    if (this.billForm.valid && this.billId) {
      const payload = {
        subtotal: this.totalAmount, // Assuming subtotal includes consultation + treatment + medicine for now
        discount: 0,
        grandTotal: this.totalAmount,
        treatmentDetails: this.treatmentDetails
      };
      
      this.consultationService.updateBill(this.billId, payload).subscribe({
        next: () => {
          this.printBill();
          this.dialogRef.close(this.billForm.value);
        },
        error: (err) => console.error('Failed to update bill', err)
      });
    }
  }

  printBill() {
    const printContents = document.getElementById('print-section')?.innerHTML;
    if (printContents) {
      const popupWin = window.open('', '_blank', 'top=0,left=0,height=auto,width=auto');
      if (popupWin) {
        popupWin.document.open();
        popupWin.document.write(`
          <html>
            <head>
              <title>Print Bill - ${this.data.patientName}</title>
              <style>
                body { font-family: sans-serif; padding: 20px; }
                table { width: 100%; border-collapse: collapse; }
                th, td { padding: 8px; border-bottom: 1px solid #ddd; }
                @media print {
                  @page { margin: 15mm; }
                  body { -webkit-print-color-adjust: exact; }
                }
              </style>
            </head>
            <body onload="window.print(); setTimeout(() => window.close(), 500);">
              ${printContents}
            </body>
          </html>
        `);
        popupWin.document.close();
      }
    }
  }
}
