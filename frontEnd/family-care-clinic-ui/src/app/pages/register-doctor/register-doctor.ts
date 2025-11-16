import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { DoctorService } from '../../services/doctor-service';

@Component({
  selector: 'app-register-doctor',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register-doctor.html',
  styleUrl: './register-doctor.css',
})
export class RegisterDoctor {

  constructor(private authService: AuthService,
    private doctorService: DoctorService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }


  formData = {
    name: '',
    email: '',
    password: '',
    licenseNumber: '',
    specialization: '',
    roles: ['DOCTOR']
  }

  specializations: string[] = [];
  error = '';
  success = '';

  ngOnInit(): void {
    this.fetchSpecializations();
  }


  fetchSpecializations(): void {
    this.doctorService.getAllSpecializationEnums().subscribe({
      next: (res: any) => {
        if (res.statusCode === 200) {
          this.specializations = res.data;
        }
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        this.error = 'Failed to load specializations. Please try again later.';
        this.cdr.detectChanges();
      }
    })
  }

  // Method to format specialization display(remove underscores)
  formatSpecialization(spec: string): string {
    return spec.replace(/_/g, ' ');
  }



  handleSubmit(event: Event): void {

    event.preventDefault();
    this.error = '';
    this.success = '';

    if (!this.formData.specialization) {
      this.error = 'Please select a specialization.';
      this.cdr.detectChanges();
      return;
    }

    this.authService.register(this.formData).subscribe({
      next: (res: any) => {
        if (res.statusCode === 200) {
          this.success = 'Registration successful! You can now log in.';
          this.formData = { name: '', email: '', password: '', licenseNumber: '', specialization: '', roles: ['DOCTOR'] };  // reset form(clears input fields)

          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 5000)
          this.cdr.detectChanges();
        } else {
          this.error = res.message || 'Registration failed. Please try again later.';
        }
      },
      error: (err: any) => {
        this.error = err.error?.message || 'An error occurred during registration. Please try again later.'; // err.error?.message: means if error is exists then get a message
        this.cdr.detectChanges();
      }
    })

  }

}
