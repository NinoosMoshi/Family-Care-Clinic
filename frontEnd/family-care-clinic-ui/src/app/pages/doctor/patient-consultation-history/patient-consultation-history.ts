import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { ConsultationService } from '../../../services/consultation-service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-patient-consultation-history',
  imports: [CommonModule],
  templateUrl: './patient-consultation-history.html',
  styleUrl: './patient-consultation-history.css',
})
export class PatientConsultationHistory {

  consultations: any[] = [];
  patient: any = null;
  error = '';
  patientId: string | null = null;


  constructor(
    private consultationService: ConsultationService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }


  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.patientId = params.get('patientId');
      if (this.patientId) {
        this.fetchConsultationHistory();
      } else {
        this.error = 'No patient ID provided';
      }
    });
  }

  fetchConsultationHistory(): void {
    this.error = '';

    this.consultationService.getConsultationHistoryForPatient(this.patientId).subscribe({
      next: (response: any) => {
        if (response.statusCode === 200) {
          this.consultations = response.data;

          // If we have consultations, extract patient info
          if (response.data.length > 0) {
            this.patient = {
              id: this.patientId
            };
          }
        }
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        this.error = 'Failed to load consultation history';
        console.error('Error fetching consultation history:', error);
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


  getTimeAgo(dateTimeString: string): string {
    const now = new Date();
    const consultationDate = new Date(dateTimeString);
    const diffTime = Math.abs(now.getTime() - consultationDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 1) return '1 day ago';
    if (diffDays < 7) return `${diffDays} days ago`;
    if (diffDays < 30) return `${Math.ceil(diffDays / 7)} weeks ago`;
    return `${Math.ceil(diffDays / 30)} months ago`;
  }

  groupConsultationsByDate(consultations: any[]): any {
    const grouped: any = {};

    consultations.forEach(consultation => {
      const date = new Date(consultation.consultationDate).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });

      if (!grouped[date]) {
        grouped[date] = [];
      }

      grouped[date].push(consultation);
    });

    return grouped;
  }

  // NEW METHOD: Convert grouped object to typed array for template
  getGroupedConsultationsArray(): any[] {
    const grouped = this.groupConsultationsByDate(this.consultations);
    return Object.keys(grouped).map(date => ({
      date,
      consultations: grouped[date]
    }));
  }


  calculateStatistics(consultations: any[]): any {
    const totalConsultations = consultations.length;
    const recentConsultations = consultations.filter(consultation => {
      const consultationDate = new Date(consultation.consultationDate);
      const thirtyDaysAgo = new Date();
      thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
      return consultationDate > thirtyDaysAgo;
    }).length;

    return {
      totalConsultations,
      recentConsultations
    };
  }

  goBackToAppointments(): void {
    this.router.navigate(['/doctor/appointments']);
  }

  getGroupedConsultations(): any {
    return this.groupConsultationsByDate(this.consultations);
  }

  getStats(): any {
    return this.calculateStatistics(this.consultations);
  }

  getMostRecentDate(): string {
    if (this.consultations.length > 0) {
      return this.formatDateTime(this.consultations[0].consultationDate);
    }
    return 'N/A';
  }

  analyzePatterns(): void {

  }


}
