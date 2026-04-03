import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SessionService } from '../../services/session.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  constructor(
    private router: Router,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    // If already logged in, redirect to articles
    if (this.sessionService.isLogged) {
      this.router.navigate(['/articles']);
    }
  }

  start() {
    this.router.navigate(['/connection']);
  }
}
