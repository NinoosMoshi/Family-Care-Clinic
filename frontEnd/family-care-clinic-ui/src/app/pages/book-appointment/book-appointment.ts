import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DoctorService } from '../../services/doctor-service';
import { Router } from '@angular/router';
import { AppointmentService } from '../../services/appointment-service';

@Component({
  selector: 'app-book-appointment',
  imports: [CommonModule, FormsModule],
  templateUrl: './book-appointment.html',
  styleUrl: './book-appointment.css',
})
export class BookAppointment {

  constructor(
    private doctorService: DoctorService,
    private appointmentService: AppointmentService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  formData: any = {
    doctorId: '',
    purposeOfConsultation: '',
    initialSymptoms: '',
    startTime: ''
  };

  doctors: any[] = [];

  error = '';
  success = '';

  ngOnInit(): void {
    this.fetchDoctors();
  }

  fetchDoctors(): void {
    this.doctorService.getAllDoctors().subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.doctors = response.data;
        }
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        this.error = 'Failed to load doctors list';
        this.cdr.detectChanges();
      }
    })
  }

  handleSubmit(event: Event): void {
    event.preventDefault();
    this.error = '';
    this.success = '';

    // Validation
    if (!this.formData.doctorId) {
      this.error = 'Please select a doctor';
      return;
    }

    if (!this.formData.startTime) {
      this.error = 'Please select appointment date and time';
      return;
    }

    // Convert local datetime to ISO format
    const appointmentData = {
      ...this.formData,
      doctorId: parseInt(this.formData.doctorId),
      // startTime: new Date(this.formData.startTime).toISOString()
      startTime: this.formData.startTime
    };

    console.log("SENDING to backend:", this.formData.startTime);
    this.appointmentService.bookAppointment(appointmentData).subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.success = 'Appointment booked successfully!';
          console.log("Appointment booked successfully")
          this.formData = {
            doctorId: '',
            purposeOfConsultation: '',
            initialSymptoms: '',
            startTime: ''
          };
          setTimeout(() => {
            this.router.navigate(['/my-appointments']);
          }, 5000);
          this.cdr.detectChanges();
        } else {
          this.error = response.message || 'Failed to book appointment';
        }
      },
      error: (error: any) => {
        this.error = error.error?.message || 'An error occurred while booking appointment';
      }
    })
  }

  handleCancel(): void {
    this.router.navigate(['/profile']);
  }

  formatSpecialization(specialization: string | undefined): string {
    if (!specialization) return '';
    return specialization.replace(/_/g, ' ');
  }

  formatDoctorName(doctor: any): string {
    if (doctor.firstName && doctor.lastName) {
      return `Dr. ${doctor.firstName} ${doctor.lastName} - ${this.formatSpecialization(doctor.specialization)}`;
    }
    return `Dr. ${doctor.user?.name} - ${this.formatSpecialization(doctor.specialization) || 'General Practice'}`;
  }

  // Get minimum datetime (current time)
  getMinDateTime(): string {
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    return now.toISOString().slice(0, 16);
  }
  todayLocal(): string {
    const now = new Date();
    return now.toISOString().slice(0, 16);
  }

}
