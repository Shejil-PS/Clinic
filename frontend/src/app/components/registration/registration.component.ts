import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
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
          this.registerVisit(patient.patientId!);
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



  isExporting: boolean = false;
  
  exportAllPatientsToCsv() {
    this.isExporting = true;
    this.snackBar.open('Fetching all patients and their history for export...', 'Close', { duration: 3000 });
    
    this.consultationService.getAllPatients().subscribe({
      next: (patients) => {
        if (!patients || patients.length === 0) {
          this.snackBar.open('No patients found in database.', 'Close', { duration: 3000 });
          this.isExporting = false;
          return;
        }

        // Fetch profile for all patients to get history
        const profileRequests = patients.map(p => this.consultationService.getPatientProfile(p.patientId!));
        
        forkJoin(profileRequests).subscribe({
          next: (profiles) => {
            const csvRows = [];
            // Headers
            csvRows.push(['Patient ID', 'Full Name', 'Phone', 'Age', 'Gender', 'Address', 'History'].join(','));
            
            // Rows
            for (let i = 0; i < patients.length; i++) {
              const p = patients[i];
              const profile = profiles[i];
              
              // Format History
              let historyString = '';
              const visitsCount = profile.previousVisits ? profile.previousVisits.length : 0;
              historyString += `Visits: ${visitsCount}`;
              
              if (profile.treatmentHistory && profile.treatmentHistory.length > 0) {
                const treatments = profile.treatmentHistory.map((t: any) => t.treatmentName).join(', ');
                historyString += ` | Treatments: ${treatments}`;
              }
              
              if (profile.prescriptionHistory && profile.prescriptionHistory.length > 0) {
                const medicines: string[] = [];
                profile.prescriptionHistory.forEach((presc: any) => {
                  if (presc.medicineList) {
                    presc.medicineList.forEach((med: any) => {
                      medicines.push(med.medicineName);
                    });
                  }
                });
                if (medicines.length > 0) {
                  historyString += ` | Medicines: ${medicines.join(', ')}`;
                }
              }

              const values = [
                p.patientId,
                `"${p.fullName}"`,
                p.phone,
                p.age,
                p.gender,
                `"${p.address || ''}"`,
                `"${historyString}"`
              ];
              csvRows.push(values.join(','));
            }
            
            const csvString = csvRows.join('\n');
            const blob = new Blob([csvString], { type: 'text/csv' });
            const url = window.URL.createObjectURL(blob);
            
            const a = document.createElement('a');
            a.setAttribute('hidden', '');
            a.setAttribute('href', url);
            a.setAttribute('download', `all_patients_with_history_${new Date().toISOString().split('T')[0]}.csv`);
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            
            this.snackBar.open(`Exported ${patients.length} patients successfully!`, 'Close', { duration: 3000, panelClass: 'success-snackbar' });
            this.isExporting = false;
          },
          error: () => {
            this.showError('Failed to fetch patient histories for export');
            this.isExporting = false;
          }
        });
      },
      error: () => {
        this.showError('Failed to fetch patients for export');
        this.isExporting = false;
      }
    });
  }
}
