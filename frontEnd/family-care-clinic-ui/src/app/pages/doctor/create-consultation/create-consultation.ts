import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ConsultationService } from '../../../services/consultation-service';
import { AppointmentService } from '../../../services/appointment-service';

@Component({
  selector: 'app-create-consultation',
  imports: [CommonModule, FormsModule],
  templateUrl: './create-consultation.html',
  styleUrl: './create-consultation.css',
})
export class CreateConsultation {

  formData: any = {
    appointmentId: '',
    subjectiveNotes: '',
    objectiveFindings: '',
    assessment: '',
    plan: ''
  };


  appointment: any | null = null;
  error = '';
  success = '';
  appointmentId: string | null = null;

  constructor(
    private consultationService: ConsultationService,
    private appointmentServie: AppointmentService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }


  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.appointmentId = params.get('appointmentId');
      if (this.appointmentId) {
        this.fetchAppointmentDetails();
      } else {
        this.error = "No Appointment ID Provided";
      }
    })
  }

  fetchAppointmentDetails(): void {
    this.appointmentServie.getMyAppointments().subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          const foundAppointment = response.data.find(
            (appt: any) => appt.id === parseInt(this.appointmentId!)
          );
          if (foundAppointment) {
            this.appointment = foundAppointment;
            this.formData = {
              ...this.formData,
              appointmentId: this.appointmentId!
            };
          }
        }
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        this.error = 'Failed to load appointment details';
        this.cdr.detectChanges();
      }
    });
  }

  handleSubmit(event: Event): void {
    event.preventDefault();
    this.error = '';
    this.success = '';

    // Validation
    if (!this.formData.subjectiveNotes || !this.formData.objectiveFindings ||
      !this.formData.assessment || !this.formData.plan) {
      this.error = 'All fields are required';
      return;
    }

    const consultationData = {
      ...this.formData,
      appointmentId: parseInt(this.formData.appointmentId)
    };

    this.consultationService.createConsultation(consultationData).subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.success = 'Consultation created successfully!';
          setTimeout(() => {
            this.router.navigate(['/doctor/appointments']);
          }, 5000);
          this.cdr.detectChanges();
        }
      },
      error: (error: any) => {
        this.error = error.error?.message || 'An error occurred while creating consultation';
        this.cdr.detectChanges();
      }
    });
  }


  handleCancel(): void {
    this.router.navigate(['/doctor/appointments']);
  }

  formatDateTime(dateTimeString: string): string {
    return new Date(dateTimeString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }


}
