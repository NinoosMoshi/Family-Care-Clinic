import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  public BASE_URL = 'http://localhost:8090/api';

  constructor(private http: HttpClient) { }


  getHeader(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  saveAuthData(token: string, roles: string[]): void {
    localStorage.setItem('token', token);
    localStorage.setItem('roles', JSON.stringify(roles));
  }



  getToken(): string | null {
    return localStorage.getItem('token');
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('roles');
  }


  hasRole(role: string): boolean {
    // 1. Retrieve the roles string from local storage
    const rolesString = localStorage.getItem('roles');

    // 2. If the string is not null or undefined, parse it into a string array.
    //    Otherwise, return false.
    const roles: string[] | null = rolesString ? JSON.parse(rolesString) : null;

    // 3. Check if the roles array exists and includes the specified role
    return roles ? roles.includes(role) : false;
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  isDoctor(): boolean {
    return this.hasRole('DOCTOR');
  }

  isPatient(): boolean {
    return this.hasRole('PATIENT');
  }


  // AUTH & USERS MANAGEMENT METHODS
  register(body: any): Observable<any> {
    return this.http.post(`${this.BASE_URL}/auth/register`, body);
  }

  login(body: any): Observable<any> {
    return this.http.post(`${this.BASE_URL}/auth/login`, body);
  }

  forgetPassword(body: any): Observable<any> {
    return this.http.post(`${this.BASE_URL}/auth/forgot-password`, body);
  }

  resetPassword(body: any): Observable<any> {
    return this.http.post(`${this.BASE_URL}/auth/reset-password`, body);
  }




}
