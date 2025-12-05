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
    { path: 'profile', component: Profile },
    { path: 'update-profile', component: UpdateProfile },
    { path: 'update-password', component: UpdatePassword },
    { path: 'book-appointment', component: BookAppointment },
    { path: 'my-appointments', component: MyAppointments },
    { path: 'consultation-history', component: ConsultationHistory },
];
