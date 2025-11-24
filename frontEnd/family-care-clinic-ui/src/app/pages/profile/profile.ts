import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { UserService } from '../../services/user-service';
import { PatientService } from '../../services/patient-service';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private patientService: PatientService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  userData: any | null = null;
  patientData: any | null = null;
  error = '';
  uploading = false;
  uploadError = '';
  uploadSuccess = '';

  ngOnInit(): void {
    this.fetchUserData();
  }


  fetchUserData(): void {
    this.error = '';
    this.userService.getMyUserDetails().subscribe({
      next: (res: any) => {
        if (res.statusCode === 200) {
          this.userData = res.data;

          // fetch patient profile if user is a patient
          if (this.userData.roles?.some((role: any) => role.name === 'PATIENT')) {
            this.fetchPatientProfile();
          }
        } else {
          this.error = 'Failed to fetch user details. Please try again later.';
        }
      },
      error: (err: any) => {
        this.error = 'An error occurred while fetching user details.';
      }
    });
  }

  fetchPatientProfile(): void {
    this.patientService.getPatientProfile().subscribe({
      next: (res: any) => {
        if (res.statusCode === 200) {
          this.patientData = res.data;
        }
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        this.error = 'An error occurred while fetching patient profile.';
        this.cdr.detectChanges();
      }
    });
  }

  handleUpdateProfile(): void {
    this.router.navigate(['/update-profile']);
  }

  handleUpdatePassword(): void {
    this.router.navigate(['/update-password']);
  }

  handleProfilePictureChange(event: any): void {

    const file = event.target.files[0];
    if (!file) return;

    // Validate file type
    const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      this.uploadError = 'Please select a valid image file (JPEG, PNG, GIF)';
      return;
    }
    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      this.uploadError = 'File size must be less than 5MB';
      return;
    }

    this.uploading = true;
    this.uploadError = '';
    this.uploadSuccess = '';

    this.userService.uploadProfilePicture(file).subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.uploadSuccess = 'Profile picture updated successfully!';
          // Refresh user data to get the new profile picture URL
          this.fetchUserData();
          // Clear the file input
          event.target.value = '';
          this.cdr.detectChanges();
        } else {
          this.uploadError = response.message || 'Failed to upload profile picture';
        }
        this.uploading = false;
      },
      error: (error: any) => {
        this.uploadError = error.error?.message || 'An error occurred while uploading the picture';
        this.uploading = false;
        this.cdr.detectChanges();
      }
    });
  }


  formatDate(dateString: string): string {
    if (!dateString) return 'Not provided';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }


  formatBloodGroup(bloodGroup: string): string {
    if (!bloodGroup) return 'Not provided';
    return bloodGroup.replace(/_/g, ' ');
  }


  // Construct full URL for profile picture
  getProfilePictureUrl(): string | null {
    if (!this.userData?.profilePictureUrl) return null;
    return this.userData.profilePictureUrl;
  }

  onImageError(event: any): void {
    event.target.style.display = 'none';
    const placeholder = event.target.nextElementSibling;
    if (placeholder) {
      placeholder.style.display = 'flex';
    }
  }

  get rolesDisplay(): string {
    if (!this.userData?.roles?.length) return 'Not provided';
    return this.userData.roles.map((role: any) => role.name).join(', ');
  }


}



