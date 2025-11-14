import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter, Subscription } from 'rxjs';
import { AuthService } from '../../services/auth-service';


@Component({
  selector: 'app-nav',
  imports: [CommonModule, RouterLink],
  templateUrl: './nav.html',
  styleUrl: './nav.css',
})
export class Nav {

  isAuthenticated = false;
  isPatient = false;
  isDoctor = false

  showLogoutModal = false;

  private routerSubscription: Subscription;

  constructor(private router: Router, private authService: AuthService) {
    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.checkAuthStatus();
      })
  }

  ngOnInit(): void {
    this.checkAuthStatus();
  }

  checkAuthStatus(): void {
    this.isAuthenticated = this.authService.isAuthenticated();
    this.isPatient = this.authService.isPatient();
    this.isDoctor = this.authService.isDoctor();
  }

  handleLogoutClick(): void {
    this.showLogoutModal = true;
  }

  handleConfirmLogout(): void {
    this.authService.logout();
    this.showLogoutModal = false;
    this.router.navigate(['/']);
    this.checkAuthStatus();
  }

  handleCancelLogout(): void {
    this.showLogoutModal = false;
  }


  isActiveLink(path: string): string {
    return this.router.url === path ? 'nav-link active' : 'nav-link';
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }

  }

}