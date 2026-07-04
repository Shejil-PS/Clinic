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
  treatmentSearch: string = '';
  treatmentNotes: string = '';
  addedTreatments: any[] = [];

  // Medicines
  availableMedicines: any[] = [];
  medicineSearch: string = '';
  medicineInstructions: string = '';
  addedPrescriptions: any[] = [];

  get filteredTreatments() {
    const term = (this.treatmentSearch || '').toLowerCase();
    return this.availableTreatments.filter(t => (t.treatmentName || '').toLowerCase().includes(term));
  }

  get filteredMedicines() {
    const term = (this.medicineSearch || '').toLowerCase();
    return this.availableMedicines.filter(m => (m.medicineName || '').toLowerCase().includes(term));
  }

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
      this.loadDraft();
    }
  }

  loadDraft() {
    const draftStr = localStorage.getItem('draft_' + this.visitId);
    if (draftStr) {
      try {
        const draft = JSON.parse(draftStr);
        this.chiefComplaint = draft.chiefComplaint || '';
        this.addedTreatments = draft.addedTreatments || [];
        this.addedPrescriptions = draft.addedPrescriptions || [];
        this.showSuccess('Draft loaded automatically');
      } catch (e) {
        console.error('Failed to parse draft', e);
      }
    }
  }

  saveDraft() {
    const draft = {
      chiefComplaint: this.chiefComplaint,
      addedTreatments: this.addedTreatments,
      addedPrescriptions: this.addedPrescriptions
    };
    localStorage.setItem('draft_' + this.visitId, JSON.stringify(draft));
    this.showSuccess('Draft saved securely');
    this.router.navigate(['/queue']);
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
    if (!this.treatmentSearch) return;
    
    const existing = this.availableTreatments.find(t => (t.treatmentName || '').toLowerCase() === this.treatmentSearch.toLowerCase());
    
    this.addedTreatments.push({
      treatmentId: existing ? existing.id : 'CUSTOM',
      treatmentName: existing ? existing.treatmentName : this.treatmentSearch,
      status: 'COMPLETED',
      cost: existing ? existing.cost : 0,
      notes: this.treatmentNotes
    });
    
    this.treatmentSearch = '';
    this.treatmentNotes = '';
    this.showSuccess('Treatment added locally');
  }

  addPrescription() {
    if (!this.medicineSearch) return;

    const existing = this.availableMedicines.find(m => (m.medicineName || '').toLowerCase() === this.medicineSearch.toLowerCase());

    this.addedPrescriptions.push({
      medicineId: existing ? existing.id : 'CUSTOM',
      medicineName: existing ? existing.medicineName : this.medicineSearch,
      dosage: '',
      duration: '',
      quantity: 1,
      instruction: this.medicineInstructions
    });

    this.medicineSearch = '';
    this.medicineInstructions = '';
    this.showSuccess('Medicine added locally');
  }

  finishConsultation() {
    this.isLoading = true;

    const payload = {
      diagnosis: '',
      notes: this.chiefComplaint,
      treatments: this.addedTreatments,
      prescription: this.addedPrescriptions.length > 0 ? {
        medicineList: this.addedPrescriptions
      } : null
    };

    this.consultationService.finishConsultation(this.visitId, payload).subscribe({
      next: () => {
        localStorage.removeItem('draft_' + this.visitId);
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
