import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {

  formData = {
    name: '',
    email: '',
    password: ''
  }

  error = '';
  success = '';

  constructor(private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }


  handleChange(event: any): void {
    const { name, value } = event.target;
    this.formData = {
      ...this.formData,
      [name]: value
    }
  }

  handleSubmit(event: Event): void {

    event.preventDefault();
    this.error = '';
    this.success = '';

    this.authService.register(this.formData).subscribe({
      next: (res: any) => {
        if (res.status === 200) {
          this.success = 'Registration successful! You can now log in.';
          this.formData = { name: '', email: '', password: '' };  // reset form(clears input fields)

          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 5000)
          this.cdr.detectChanges();
        } else {
          this.error = res.message || 'Registration failed. Please try again later.';
        }
      },
      error: (err: any) => {
        this.error = err.error?.message || 'An error occurred during registration. Please try again later.';
        this.cdr.detectChanges();
      }
    })

  }



}
