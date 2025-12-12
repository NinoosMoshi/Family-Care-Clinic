import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DoctorService } from '../../../services/doctor-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-update-doctor-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './update-doctor-profile.html',
  styleUrl: './update-doctor-profile.css',
})
export class UpdateDoctorProfile {

  formData = {
    firstName: '',
    lastName: '',
    specialization: ''
  };

  specializations: string[] = [];
  error = '';
  success = '';

  constructor(
    private doctorService: DoctorService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.fetchProfileData();
    this.fetchSpecializations();
  }


  fetchProfileData(): void {
    this.doctorService.getDoctorProfile().subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          const doctorData = response.data;
          this.formData = {
            firstName: doctorData.firstName || '',
            lastName: doctorData.lastName || '',
            specialization: doctorData.specialization || ''
          };
          this.cdr.detectChanges();
        }
      },
      error: (error: any) => {
        this.error = 'Failed to load profile data';
        this.cdr.detectChanges();
      }
    });
  }

  fetchSpecializations(): void {
    this.doctorService.getAllSpecializationEnums().subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.specializations = response.data;
        }
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        this.error = 'Failed to load specializations';
        this.cdr.detectChanges();
      }
    });
  }

  handleCancel(): void {
    this.router.navigate(['/doctor/profile']);
  }

  formatSpecialization(spec: string): string {
    return spec.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
  }


  handleSubmit(event: Event): void {
    event.preventDefault();
    this.error = '';
    this.success = '';

    this.doctorService.updateDoctorProfile(this.formData).subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.success = 'Profile updated successfully!';
          setTimeout(() => {
            this.router.navigate(['/doctor/profile']);
          }, 5000);
          this.cdr.detectChanges();
        }
      },
      error: (error: any) => {
        this.error = error.error?.message || 'An error occurred while updating profile';
        this.cdr.detectChanges();
      }
    });
  }

}
