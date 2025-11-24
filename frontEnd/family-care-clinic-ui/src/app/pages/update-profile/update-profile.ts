import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { PatientService } from '../../services/patient-service';

@Component({
  selector: 'app-update-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './update-profile.html',
  styleUrl: './update-profile.css',
})
export class UpdateProfile {

  constructor(
    private authService: AuthService,
    private patientService: PatientService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  formData = {
    firstName: '',
    lastName: '',
    phone: '',
    dateOfBirth: '',
    knownAllergies: '',
    bloodGroup: '',
    genotype: ''
  };

  bloodGroups: string[] = [];
  genotypes: string[] = [];

  error = '';
  success = '';


  ngOnInit(): void {
    this.fetchEnums();
    this.fetchProfileData();
  }


  fetchProfileData(): void {
    this.patientService.getPatientProfile().subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          const patientData = response.data;

          this.formData = {
            firstName: patientData.firstName || '',
            lastName: patientData.lastName || '',
            phone: patientData.phone || '',
            dateOfBirth: patientData.dateOfBirth || '',
            knownAllergies: patientData.knownAllergies || '',
            bloodGroup: patientData.bloodGroup || '',
            genotype: patientData.genotype || ''
          };
        }
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        this.error = 'Failed to load profile data';
        this.cdr.detectChanges();
      }
    });
  }

  fetchEnums(): void {

    const bloodGroupRequest = this.patientService.getAllBloodGroupEnums();
    const genotypeRequest = this.patientService.getAllGenotypeEnums();

    bloodGroupRequest.subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.bloodGroups = response.data;
        }
      },
      error: (error: any) => {
        this.error = 'Failed to load blood group options';
      }
    });

    genotypeRequest.subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.genotypes = response.data;
        }
      },
      error: (error: any) => {
        this.error = 'Failed to load genotype options';
      }
    });

    this.cdr.detectChanges();
  }


  handleSubmit(event: Event): void {
    event.preventDefault();
    this.error = '';
    this.success = '';

    this.patientService.updatePatientProfile(this.formData).subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.success = 'Profile updated successfully!';
          setTimeout(() => {
            this.router.navigate(['/profile']);
          }, 1500);
        } else {
          this.error = response.message || 'Failed to update profile';
        }
      },
      error: (error: any) => {
        this.error = error.error?.message || 'An error occurred while updating profile';
      }
    });
  }


  formatBloodGroup(group: string): string {
    return group.replace(/_/g, ' ');
  }

  handleCancel(): void {
    this.router.navigate(['/profile']);
  }



}
