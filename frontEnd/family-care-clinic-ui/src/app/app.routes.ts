import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Register } from './pages/register/register';

export const routes: Routes = [

    // AUTH ROUTES
    { path: 'register', component: Register },


    { path: 'home', component: Home },
    { path: '', redirectTo: 'home', pathMatch: 'full' },
];
