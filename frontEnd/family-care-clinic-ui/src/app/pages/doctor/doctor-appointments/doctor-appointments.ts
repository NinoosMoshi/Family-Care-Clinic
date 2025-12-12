import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DoctorService } from '../../../services/doctor-service';
import { AppointmentService } from '../../../services/appointment-service';

@Component({
  selector: 'app-doctor-appointments',
  imports: [CommonModule, RouterLink],
  templateUrl: './doctor-appointments.html',
  styleUrl: './doctor-appointments.css',
})
export class DoctorAppointments {


  appointments: any[] = [];
  error = '';

  constructor(
    private appointmentService: AppointmentService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.fetchAppointments();
  }

  fetchAppointments(): void {
    this.error = '';

    this.appointmentService.getMyAppointments().subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.appointments = response.data;
        }
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        this.error = 'Failed to load appointments';
        this.cdr.detectChanges();
      }
    });
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

  getStatusBadge(status: string): { class: string, text: string } {
    const statusConfig: { [key: string]: { class: string, text: string } } = {
      'SCHEDULED': { class: 'status-scheduled', text: 'Scheduled' },
      'COMPLETED': { class: 'status-completed', text: 'Completed' },
      'CANCELLED': { class: 'status-cancelled', text: 'Cancelled' },
      'IN_PROGRESS': { class: 'status-in-progress', text: 'In Progress' }
    };

    return statusConfig[status] || { class: 'status-default', text: status };
  }

  handleCompleteAppointment(appointmentId: number): void {
    if (!window.confirm('Are you sure you want to mark this appointment as completed?')) {
      return;
    }

    this.appointmentService.completeAppointment(appointmentId).subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          // Refresh appointments list
          this.fetchAppointments();
          this.cdr.detectChanges();
        }
      },
      error: (error: any) => {
        console.log(error)
        this.error = "Error updating the appointment status";
        this.cdr.detectChanges();
      }
    });
  }


  handleCancelAppointment(appointmentId: number): void {
    if (!window.confirm('Are you sure you want to cancel this appointment?')) {
      return;
    }

    this.appointmentService.cancelAppointment(appointmentId).subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          // Refresh appointments list
          this.fetchAppointments();
          this.cdr.detectChanges();
        }
      },
      error: (error: any) => {
        this.error = "Error Cancelling the appointment";
        this.cdr.detectChanges();
      }
    });
  }

  formatPatientInfo(patient: any): string {
    return `${patient.firstName} ${patient.lastName} (${patient.user?.email})`;
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'Not provided';
    return new Date(dateString).toLocaleDateString();
  }

  formatBloodGroup(bloodGroup: string | undefined): string {
    if (!bloodGroup) return 'Not provided';
    return bloodGroup.replace(/_/g, ' ');
  }



}
