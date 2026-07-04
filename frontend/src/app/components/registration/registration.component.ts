import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ConsultationService } from '../../core/services/consultation.service';
import { Patient } from '../../core/models/patient.model';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent {
  searchPhone: string = '';
  isLoading: boolean = false;
  searchAttempted: boolean = false;
  
  patientFound: boolean = false;
  existingPatient: Patient | null = null;
  
  isNewPatient: boolean = false;
  registrationForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private consultationService: ConsultationService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.registrationForm = this.fb.group({
      fullName: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      age: ['', [Validators.required, Validators.min(0)]],
      gender: ['', Validators.required],
      address: ['']
    });
  }

  searchPatient() {
    if (!this.searchPhone) return;
    
    this.isLoading = true;
    this.searchAttempted = true;
    
    this.consultationService.searchPatientByPhone(this.searchPhone).subscribe({
      next: (patient) => {
        this.patientFound = true;
        this.existingPatient = patient;
        this.isLoading = false;
      },
      error: (err) => {
        if (err.status === 404) {
          this.patientFound = false;
        } else {
          this.showError('Error searching for patient');
        }
        this.isLoading = false;
      }
    });
  }

  startNewRegistration() {
    this.isNewPatient = true;
    this.registrationForm.patchValue({ phone: this.searchPhone });
  }

  resetSearch() {
    this.searchPhone = '';
    this.searchAttempted = false;
    this.patientFound = false;
    this.existingPatient = null;
    this.isNewPatient = false;
    this.registrationForm.reset();
  }

  onSubmitNewPatient() {
    if (this.registrationForm.valid) {
      this.isLoading = true;
      const newPatient: Patient = this.registrationForm.value;
      
      this.consultationService.registerPatient(newPatient).subscribe({
        next: (patient) => {
          // Immediately register a visit for the new patient
          this.registerVisit(patient.id!);
        },
        error: () => {
          this.showError('Failed to register patient');
          this.isLoading = false;
        }
      });
    }
  }

  registerVisit(patientId: string) {
    this.isLoading = true;
    this.consultationService.registerVisit(patientId).subscribe({
      next: () => {
        this.showSuccess('Visit registered successfully! Status is WAITING.');
        this.resetSearch();
        this.router.navigate(['/queue']); // Redirect to Consultation Queue (Phase 4)
      },
      error: () => {
        this.showError('Failed to register visit');
        this.isLoading = false;
      }
    });
  }

  private showSuccess(message: string) {
    this.snackBar.open(message, 'Close', { duration: 4000, panelClass: 'success-snackbar' });
  }

  private showError(message: string) {
    this.snackBar.open(message, 'Close', { duration: 4000, panelClass: 'error-snackbar' });
  }
}
