import { Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DoctorService {

  private BASE_URL = environment.BASE_URL;

  constructor(private http: HttpClient, private authService: AuthService) { }


  getDoctorProfile(): Observable<any> {
    return this.http.get(`${this.BASE_URL}/doctors/me`,
      { headers: this.authService.getHeader() });
  }

  updateDoctorProfile(body: any): Observable<any> {
    return this.http.put(`${this.BASE_URL}/doctors/me`, body,
      { headers: this.authService.getHeader() });
  }

  getAllDoctors(): Observable<any> {
    return this.http.get(`${this.BASE_URL}/doctors/all`,
      { headers: this.authService.getHeader() });
  }

  getDoctorById(doctorId: number | string): Observable<any> {
    return this.http.get(`${this.BASE_URL}/doctors/${doctorId}`,
      { headers: this.authService.getHeader() });
  }

  getAllSpecializationEnums(): Observable<any> {
    return this.http.get(`${this.BASE_URL}/doctors/specializations`)
  }

}
