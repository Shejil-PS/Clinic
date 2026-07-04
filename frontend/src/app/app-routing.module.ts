import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { LayoutComponent } from './components/layout/layout.component';
import { MedicineMasterComponent } from './components/medicine-master/medicine-master.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { ConsultationQueueComponent } from './components/consultation-queue/consultation-queue.component';
import { ConsultationScreenComponent } from './components/consultation-screen/consultation-screen.component';
import { ConsultedPatientsComponent } from './components/consulted-patients/consulted-patients.component';
import { AuthGuard } from './core/auth/auth.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { 
    path: '', 
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'medicine-master', component: MedicineMasterComponent },
      { path: 'registration', component: RegistrationComponent },
      { path: 'queue', component: ConsultationQueueComponent },
      { path: 'consultation/:visitId', component: ConsultationScreenComponent },
      { path: 'consulted', component: ConsultedPatientsComponent }
    ]
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
