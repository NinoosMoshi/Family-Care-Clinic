import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Register } from './pages/register/register';
import { RegisterDoctor } from './pages/register-doctor/register-doctor';
import { Login } from './pages/login/login';

export const routes: Routes = [

    // AUTH ROUTES
    { path: 'register', component: Register },
    { path: 'register-doctor', component: RegisterDoctor },
    { path: 'login', component: Login },


    { path: 'home', component: Home },
    { path: '', redirectTo: 'home', pathMatch: 'full' },
];
