import { Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AppointmentService {

  private BASE_URL = environment.BASE_URL;


  constructor(private http: HttpClient, private authService: AuthService) { }

  bookAppointment(appointmentDTO: any): Observable<any> {
    return this.http.post(`${this.BASE_URL}/appointments`, appointmentDTO,
      { headers: this.authService.getHeader() });
  }

  getMyAppointments(): Observable<any> {
    return this.http.get(`${this.BASE_URL}/appointments`,
      { headers: this.authService.getHeader() });
  }

  cancelAppointment(appointmentId: number | string): Observable<any> {
    // Angular's HtttpClient put typically requires a body. if your API expects an empty for this endpoint,
    // you can pass an empty object `{}` as the body.
    return this.http.put(`${this.BASE_URL}/appointments/cancel/${appointmentId}`, {},
      { headers: this.authService.getHeader() });
  }

  completeAppointment(appointmentId: number | string): Observable<any> {
    return this.http.put(`${this.BASE_URL}/appointments/complete/${appointmentId}`, {},
      { headers: this.authService.getHeader() });
  }



}
