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
  existingPatients: Patient[] = [];
  
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
      next: (patients) => {
        if (patients && patients.length > 0) {
          this.patientFound = true;
          this.existingPatients = patients;
        } else {
          this.patientFound = false;
          this.existingPatients = [];
        }
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
    this.existingPatients = [];
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
    this.snackBar.open('Fetching all patients for export...', 'Close', { duration: 3000 });
    
    this.consultationService.getAllPatients().subscribe({
      next: async (patients) => {
        if (!patients || patients.length === 0) {
          this.snackBar.open('No patients found in database.', 'Close', { duration: 3000 });
          this.isExporting = false;
          return;
        }

        const csvRows = [];
        // Headers
        csvRows.push(['Patient ID', 'Full Name', 'Phone', 'Age', 'Gender', 'Address'].join(','));
        
        // Rows
        for (let i = 0; i < patients.length; i++) {
          const p = patients[i];
          const values = [
            p.patientId,
            `"${p.fullName}"`,
            p.phone,
            p.age,
            p.gender,
            `"${p.address || ''}"`
          ];
          csvRows.push(values.join(','));
        }
        
        const csvString = csvRows.join('\n');
        
        try {
          if ('showSaveFilePicker' in window) {
            const handle = await (window as any).showSaveFilePicker({
              suggestedName: 'all_patients.csv',
              types: [{
                description: 'CSV File',
                accept: {'text/csv': ['.csv']}
              }]
            });
            const writable = await handle.createWritable();
            await writable.write(csvString);
            await writable.close();
          } else {
            // Fallback for older browsers
            const blob = new Blob([csvString], { type: 'text/csv' });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.setAttribute('hidden', '');
            a.setAttribute('href', url);
            a.setAttribute('download', 'all_patients.csv');
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
          }
          this.snackBar.open(`Exported ${patients.length} patients successfully!`, 'Close', { duration: 3000, panelClass: 'success-snackbar' });
        } catch (err: any) {
          if (err.name !== 'AbortError') {
            this.showError('Export failed: ' + err.message);
          }
        } finally {
          this.isExporting = false;
        }
      },
      error: () => {
        this.showError('Failed to fetch patients for export');
        this.isExporting = false;
      }
    });
  }
}
