import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { DoctorService } from '../../../services/doctor-service';
import { UserService } from '../../../services/user-service';

@Component({
  selector: 'app-doctor-profile',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './doctor-profile.html',
  styleUrl: './doctor-profile.css',
})
export class DoctorProfile {

  userData: any | null = null;
  doctorData: any | null = null;

  error = '';
  uploading = false;
  uploadError = '';
  uploadSuccess = '';

  constructor(
    private doctorService: DoctorService,
    private userService: UserService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }


  ngOnInit(): void {
    this.fetchDoctorData();
  }

  fetchDoctorData() {
    this.error = '';

    this.userService.getMyUserDetails().subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.userData = response.data
          this.fetchDoctorProfile();
        }

      },
      error: (error: any) => {
        this.error = 'Failed to load profile data';
        console.error('Error fetching doctor profile:', error);
        this.cdr.detectChanges();
      }
    })
  }

  fetchDoctorProfile() {
    this.doctorService.getDoctorProfile().subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.doctorData = response.data;
        }
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        console.error('Error fetching doctor profile:', error);
        this.cdr.detectChanges();
      }
    });
  }

  handleUpdateProfile(): void {
    this.router.navigate(['/doctor/update-profile']);
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
          this.fetchDoctorData();
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


  formatSpecialization(spec: string | undefined): string {
    if (!spec) return 'Not specified';
    return spec.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
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
