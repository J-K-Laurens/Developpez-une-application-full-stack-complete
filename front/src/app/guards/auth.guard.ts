import { inject, Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { SessionService } from '../services/session.service';
import { Observable } from 'rxjs';
import { take, map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  private router = inject(Router);
  private sessionService = inject(SessionService);

  public canActivate(): Observable<boolean> {
    // Subscribe to session state and allow only if logged in
    return this.sessionService.$isLogged().pipe(
      take(1),
      map((isLogged) => {
        if (!isLogged) {
          this.router.navigate(['connection']);
          return false;
        }
        return true;
      })
    );
  }
}