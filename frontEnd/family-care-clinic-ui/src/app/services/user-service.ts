import { Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root',
})
export class UserService {

  private BASE_URL = environment.BASE_URL;


  constructor(private http: HttpClient, private authService: AuthService) { }


  getMyUserDetails(): Observable<any> {
    return this.http.get(`${this.BASE_URL}/users/me`,
      { headers: this.authService.getHeader() });
  }


  getUserById(userId: number | string): Observable<any> {
    return this.http.get(`${this.BASE_URL}/users/by-id/${userId}`,
      { headers: this.authService.getHeader() })
  }


  getAllUsers(): Observable<any> {
    return this.http.get(`${this.BASE_URL}/users/all`,
      { headers: this.authService.getHeader() });
  }


  updatePassword(updatePasswordRequest: any): Observable<any> {
    return this.http.put(`${this.BASE_URL}/users/update-password`, updatePasswordRequest,
      { headers: this.authService.getHeader() });
  }


  uploadProfilePicture(file: File): Observable<any> {

    const formData: FormData = new FormData();
    formData.append('file', file);

    const headers = this.authService.getHeader();

    return this.http.put(`${this.BASE_URL}/users/profile-picture`, formData,
      { headers });
  }







}
