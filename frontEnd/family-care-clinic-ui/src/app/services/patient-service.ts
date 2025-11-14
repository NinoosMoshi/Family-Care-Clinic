import { Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PatientService {

  private BASE_URL = environment.BASE_URL;

  constructor(private http: HttpClient, private authService: AuthService) { }

  getPatientProfile(): Observable<any> {
    return this.http.get(`${this.BASE_URL}/patients/me`,
      { headers: this.authService.getHeader() });
  }

  updatePatientProfile(body: any): Observable<any> {
    return this.http.put(`${this.BASE_URL}/patients/me`, body,
      { headers: this.authService.getHeader() });
  }

  getPatientById(patientId: number | string): Observable<any> {
    return this.http.get(`${this.BASE_URL}/patients/${patientId}`,
      { headers: this.authService.getHeader() });
  }

  getAllGenotypeEnums(): Observable<any> {
    return this.http.get(`${this.BASE_URL}/patients/genotype`,
      { headers: this.authService.getHeader() });
  }

  getAllBloodGroupEnums(): Observable<any> {
    return this.http.get(`${this.BASE_URL}/patients/blood-group`,
      { headers: this.authService.getHeader() });
  }








}
