import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SessionService } from 'src/app/services/session.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {

  constructor(
    private sessionService: SessionService,
    private router: Router
  ) {}

  get isLogged(): boolean {
    return this.sessionService.isLogged;
  }

  logout(): void {
    this.sessionService.logOut();
    this.router.navigate(['/connection']);
  }
}
