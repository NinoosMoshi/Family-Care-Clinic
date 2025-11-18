import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  constructor(private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  formData = {
    email: '',
    password: ''
  }

  error = '';

  handleChange(event: any): void {
    const { name, value } = event.target;
    this.formData = {
      ...this.formData,
      [name]: value
    }
  }


  handleSubmit(event: any) {
    event.preventDefault();
    this.error = '';

    this.authService.login(this.formData).subscribe({
      next: (res: any) => {
        if (res.statusCode === 200) {
          const { token, roles } = res.data;
          this.authService.saveAuthData(token, roles);
          this.router.navigate(['/']);
        }
        else {
          this.error = res.message || 'Login failed.';
          this.cdr.detectChanges();
        }
      },
      error: (err: any) => {
        this.error = err.error?.message || 'An error occurred during login.';
        this.cdr.detectChanges();
      }
    })

  }




}
