import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-reset-password',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword {

  formData: any = {
    newPassword: '',
    confirmPassword: '',
    code: ''
  };

  error = '';
  success = '';

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    // Get code from query parameters
    this.route.queryParamMap.subscribe(params => {
      const codeFromUrl = params.get('code');
      if (codeFromUrl) {
        this.formData = {
          ...this.formData,
          code: codeFromUrl
        };
      }
    });
  }


  handleSubmit(event: Event): void {
    event.preventDefault();
    this.error = '';
    this.success = '';

    // Validation
    if (!this.formData.code) {
      this.error = 'Reset code is missing. Please use the link from your email.';
      this.cdr.detectChanges();
      return;
    }

    if (this.formData.newPassword !== this.formData.confirmPassword) {
      this.error = 'New password and confirm password do not match';
      this.cdr.detectChanges();
      return;
    }

    if (this.formData.newPassword.length < 4) {
      this.error = 'New password must be at least 4 characters long';
      this.cdr.detectChanges();
      return;
    }

    const resetData = {
      newPassword: this.formData.newPassword,
      code: this.formData.code
    };

    this.authService.resetPassword(resetData).subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.success = 'Password reset successfully! You can now login with your new password.';
          this.formData = {
            newPassword: '',
            confirmPassword: '',
            code: this.formData.code // Keep code for display
          };

          // Redirect to login after 5 seconds
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);

          this.authService.logout();

          this.cdr.detectChanges();
        } else {
          this.error = response.message || 'Failed to reset password';
          this.cdr.detectChanges();
        }
      },
      error: (error: any) => {
        this.error = error.error?.message || 'An error occurred while resetting your password';
        this.cdr.detectChanges();
      }
    });

  }


  // Computed properties for password requirements
  get isPasswordLengthValid(): boolean {
    return this.formData.newPassword.length >= 4;
  }

  get doPasswordsMatch(): boolean {
    return this.formData.newPassword === this.formData.confirmPassword &&
      this.formData.newPassword.length > 0;
  }


}
