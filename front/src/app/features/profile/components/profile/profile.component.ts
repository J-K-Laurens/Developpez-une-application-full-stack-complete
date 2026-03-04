import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SessionService } from 'src/app/services/session.service';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { ProfileService } from '../../services/profile.service';
import { Subscription } from '../../interfaces/subscription.interface';
import { PasswordValidator } from 'src/app/features/auth/validators/password.validator';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  profileForm: FormGroup;
  subscriptions: Subscription[] = [];
  successMessage: string | null = null;
  errorMessage: string | null = null;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private sessionService: SessionService,
    private authService: AuthService,
    private profileService: ProfileService
  ) {
    this.profileForm = this.fb.group({
      firstName: [''],
      email: [''],
      password: ['', [PasswordValidator.strength()]]
    });
  }

  ngOnInit(): void {
    const user = this.sessionService.user;
    if (user) {
      this.profileForm.patchValue({
        firstName: user.firstName ?? '',
        email: user.email ?? ''
      });
    }
    this.loadSubscriptions();
  }

  loadSubscriptions(): void {
    this.profileService.getSubscriptions().subscribe({
      next: (subs) => this.subscriptions = subs,
      error: () => this.subscriptions = []
    });
  }

  save(): void {
    this.isLoading = true;
    this.successMessage = null;
    this.errorMessage = null;

    const { firstName, email, password } = this.profileForm.value;
    const request: any = {};
    if (firstName) request['firstName'] = firstName;
    if (email) request['email'] = email;
    if (password) request['password'] = password;

    this.profileService.updateProfile(request).subscribe({
      next: (updatedUser) => {
        this.isLoading = false;
        this.successMessage = 'Profil mis à jour avec succès.';
        this.authService.me().subscribe(user => this.sessionService.logIn(user));
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.status === 409
          ? 'Cet email est déjà utilisé.'
          : err.error?.message ?? 'Une erreur est survenue.';
      }
    });
  }

  unsubscribe(topicId: number): void {
    this.profileService.unsubscribe(topicId).subscribe({
      next: () => {
        this.subscriptions = this.subscriptions.filter(s => s.id !== topicId);
      }
    });
  }
}
