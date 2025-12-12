import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Register } from './pages/register/register';
import { RegisterDoctor } from './pages/register-doctor/register-doctor';
import { Login } from './pages/login/login';
import { Profile } from './pages/profile/profile';
import { UpdateProfile } from './pages/update-profile/update-profile';
import { UpdatePassword } from './pages/update-password/update-password';
import { BookAppointment } from './pages/book-appointment/book-appointment';
import { MyAppointments } from './pages/my-appointments/my-appointments';
import { ConsultationHistory } from './pages/consultation-history/consultation-history';
import { ForgotPassword } from './pages/forgot-password/forgot-password';
import { ResetPassword } from './pages/reset-password/reset-password';
import { authGuard, doctorOnlyGuard, patientOnlyGuard } from './guards/guard';
import { DoctorProfile } from './pages/doctor/doctor-profile/doctor-profile';
import { UpdateDoctorProfile } from './pages/doctor/update-doctor-profile/update-doctor-profile';
import { DoctorAppointments } from './pages/doctor/doctor-appointments/doctor-appointments';


export const routes: Routes = [

    // AUTH ROUTES
    { path: 'register', component: Register },
    { path: 'register-doctor', component: RegisterDoctor },
    { path: 'login', component: Login },
    { path: 'forgot-password', component: ForgotPassword },
    { path: 'reset-password', component: ResetPassword },
    { path: 'home', component: Home },
    { path: '', redirectTo: 'home', pathMatch: 'full' },

    // PROFILE ROUTES
    { path: 'profile', component: Profile, canActivate: [patientOnlyGuard] },
    { path: 'update-profile', component: UpdateProfile, canActivate: [patientOnlyGuard] },
    { path: 'update-password', component: UpdatePassword, canActivate: [authGuard] },
    { path: 'book-appointment', component: BookAppointment, canActivate: [patientOnlyGuard] },
    { path: 'my-appointments', component: MyAppointments, canActivate: [patientOnlyGuard] },
    { path: 'consultation-history', component: ConsultationHistory, canActivate: [patientOnlyGuard] },

    /* Doctors Routes */
    { path: 'doctor/profile', component: DoctorProfile, canActivate: [doctorOnlyGuard] },
    { path: 'doctor/update-profile', component: UpdateDoctorProfile, canActivate: [doctorOnlyGuard] },
    { path: 'doctor/appointments', component: DoctorAppointments, canActivate: [doctorOnlyGuard] },
    // { path: 'doctor/create-consultation', component: CreateConsultation, canActivate: [doctorOnlyGuard] },
    // { path: 'doctor/patient-consultation-history', component: PatientConsultationHistory, canActivate: [doctorOnlyGuard] },



    { path: '**', redirectTo: 'home', pathMatch: 'full' },
];
