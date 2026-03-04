import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SessionService } from 'src/app/services/session.service';
import { PasswordValidator } from '../../validators/password.validator';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  errorMessage: string | null = null;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private sessionService: SessionService
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, PasswordValidator.strength()]],
    });
  }

  ngOnInit(): void {}

  back(): void {
    this.router.navigate(['/connection']);
  }

  public submit(): void {
    if (this.registerForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = null;

    const { username, email, password } = this.registerForm.value;

    this.authService.register({ name: username, email, password }).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        this.authService.me().subscribe({
          next: (user) => {
            this.sessionService.logIn(user);
            this.router.navigate(['/articles']);
          },
          error: () => {
            this.router.navigate(['/articles']);
          }
        });
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 409) {
          this.errorMessage = 'Un compte existe déjà avec cet email.';
        } else if (err.status === 400) {
          this.errorMessage = err.error?.message ?? 'Données invalides (email, nom ou mot de passe incorrect).';
        } else if (err.status === 0) {
          this.errorMessage = 'Impossible de joindre le serveur. Vérifiez que l\'API est lancée.';
        } else {
          this.errorMessage = `Erreur ${err.status} : ${err.error?.message ?? 'Veuillez réessayer.'}`;
        }
      }
    });
  }
}