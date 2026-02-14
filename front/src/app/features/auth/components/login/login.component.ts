import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      emailOrUsername: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  ngOnInit(): void {}

  back() {
    this.router.navigate(['/connection']);
  }

  public submit(): void {
    if (this.loginForm.valid) {
      // Faire l'appel à l'API de connexion (authService, sessionService, etc.)
    }
  }
}
