import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SessionService } from 'src/app/services/session.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  menuOpen = false;

  constructor(
    private sessionService: SessionService,
    private router: Router
  ) {}

  get isLogged(): boolean {
    return this.sessionService.isLogged;
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  closeMenu(): void {
    this.menuOpen = false;
  }

  logout(): void {
    this.sessionService.logOut();
    this.router.navigate(['/connection']);
    this.closeMenu();
  }
}
