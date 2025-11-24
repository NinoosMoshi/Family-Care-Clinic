import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../services/user-service';

@Component({
  selector: 'app-update-password',
  imports: [CommonModule, FormsModule],
  templateUrl: './update-password.html',
  styleUrl: './update-password.css',
})
export class UpdatePassword {

  constructor(
    private userService: UserService,
    private router: Router
  ) { }


  formData = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  error = '';
  success = '';

  handleSubmit(event: Event): void {
    event.preventDefault();
    this.error = '';
    this.success = '';

    // Validation
    if (this.formData.newPassword !== this.formData.confirmPassword) {
      this.error = 'New password and confirm password do not match';
      return;
    }

    if (this.formData.newPassword.length < 4) {
      this.error = 'New password must be at least 4 characters long';
      return;
    }

    const updatePasswordRequest = {
      oldPassword: this.formData.oldPassword,
      newPassword: this.formData.newPassword
    };

    this.userService.updatePassword(updatePasswordRequest).subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.success = 'Password updated successfully!';
          this.formData = {
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
          };
          setTimeout(() => {
            this.router.navigate(['/profile']);
          }, 1500);
        } else {
          this.error = response.message || 'Failed to update password';
        }
      },
      error: (error: any) => {
        this.error = error.error?.message || 'An error occurred while updating password';
      }
    });
  }

  handleCancel(): void {
    this.router.navigate(['/profile']);
  }

}
