import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConsultationService } from '../../core/services/consultation.service';
import { MedicineService } from '../../core/services/medicine.service';
import { TreatmentService } from '../../core/services/treatment.service';
import { Visit } from '../../core/models/patient.model';

@Component({
  selector: 'app-consultation-screen',
  templateUrl: './consultation-screen.component.html',
  styleUrls: ['./consultation-screen.component.scss']
})
export class ConsultationScreenComponent implements OnInit {
  visitId!: string;
  visit!: Visit;
  isLoading = false;

  chiefComplaint: string = '';

  // Treatments
  availableTreatments: any[] = [];
  selectedTreatmentId: string = '';
  treatmentNotes: string = '';
  addedTreatments: any[] = [];

  // Medicines
  availableMedicines: any[] = [];
  selectedMedicineId: string = '';
  medicineInstructions: string = '';
  addedPrescriptions: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private consultationService: ConsultationService,
    private medicineService: MedicineService,
    private treatmentService: TreatmentService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.visitId = this.route.snapshot.paramMap.get('visitId')!;
    if (this.visitId) {
      this.loadVisit();
      this.loadMasters();
    }
  }

  loadVisit() {
    this.consultationService.getVisitById(this.visitId).subscribe({
      next: (res) => this.visit = res,
      error: () => this.showError('Failed to load visit details')
    });
  }

  loadMasters() {
    this.medicineService.getAllMedicines(0, 1000).subscribe(res => {
      this.availableMedicines = res.content;
    });
    this.treatmentService.getAllTreatments().subscribe(res => {
      // The API returns the list directly or wrapped in a page. Assuming page for consistency or array based on backend.
      // If it's a page: res.content. If array: res.
      this.availableTreatments = res.content || res;
    });
  }

  addTreatment() {
    if (!this.selectedTreatmentId) return;
    
    const treatmentReq = {
      visitId: this.visitId,
      patientId: this.visit.patientId,
      treatmentMasterId: this.selectedTreatmentId,
      notes: this.treatmentNotes
    };

    this.consultationService.addTreatment(treatmentReq).subscribe({
      next: (res) => {
        this.addedTreatments.push({ ...res, notes: this.treatmentNotes });
        this.selectedTreatmentId = '';
        this.treatmentNotes = '';
        this.showSuccess('Treatment added');
      },
      error: () => this.showError('Failed to add treatment')
    });
  }

  addPrescription() {
    if (!this.selectedMedicineId) return;

    const req = {
      visitId: this.visitId,
      patientId: this.visit.patientId,
      medicineMasterId: this.selectedMedicineId,
      instructions: this.medicineInstructions
    };

    this.consultationService.addPrescription(req).subscribe({
      next: (res) => {
        this.addedPrescriptions.push({ ...res, instructions: this.medicineInstructions });
        this.selectedMedicineId = '';
        this.medicineInstructions = '';
        this.showSuccess('Prescription added');
      },
      error: () => this.showError('Failed to add prescription')
    });
  }

  finishConsultation() {
    this.isLoading = true;
    this.consultationService.finishConsultation(this.visitId).subscribe({
      next: () => {
        this.showSuccess('Consultation finished successfully');
        this.router.navigate(['/queue']);
      },
      error: () => {
        this.showError('Failed to finish consultation');
        this.isLoading = false;
      }
    });
  }

  private showSuccess(msg: string) {
    this.snackBar.open(msg, 'Close', { duration: 3000, panelClass: 'success-snackbar' });
  }

  private showError(msg: string) {
    this.snackBar.open(msg, 'Close', { duration: 3000, panelClass: 'error-snackbar' });
  }
}
