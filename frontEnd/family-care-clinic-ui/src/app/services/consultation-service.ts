import { Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ConsultationService {

  private BASE_URL = environment.BASE_URL;


  constructor(private http: HttpClient, private authService: AuthService) { }


  createConsultation(consultationData: any): Observable<any> {
    return this.http.post(`${this.BASE_URL}/consultations`, consultationData,
      { headers: this.authService.getHeader() });
  }


  getConsultationByAppointmentId(appointmentId: number | string): Observable<any> {
    return this.http.get(`${this.BASE_URL}/consultations/appointment/${appointmentId}`,
      { headers: this.authService.getHeader() });
  }


  getConsultationHistoryForPatient(patientId?: number | string | null): Observable<any> { // ? mean The method can be called with patientId(number or string or null) or without patientId
    let params = new HttpParams();
    // only set the parameter if it's provided
    if (patientId) {
      params = params.set('patientId', String(patientId));
    }

    return this.http.get(`${this.BASE_URL}/consultations/history`,
      {
        headers: this.authService.getHeader(),
        params: params
      });
  }


}



