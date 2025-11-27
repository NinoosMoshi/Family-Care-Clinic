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


export const routes: Routes = [

    // AUTH ROUTES
    { path: 'register', component: Register },
    { path: 'register-doctor', component: RegisterDoctor },
    { path: 'login', component: Login },


    { path: 'home', component: Home },
    { path: '', redirectTo: 'home', pathMatch: 'full' },

    // PROFILE ROUTES
    { path: 'profile', component: Profile },
    { path: 'update-profile', component: UpdateProfile },
    { path: 'update-password', component: UpdatePassword },
    { path: 'book-appointment', component: BookAppointment },
    { path: 'my-appointments', component: MyAppointments },
];
