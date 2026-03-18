import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { SessionService } from '../services/session.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(
    private router: Router,
    private sessionService: SessionService
  ) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // 401 = non authentifiée → redirection login
        if (error.status === 401) {
          // Ne pas déconnecter si la requête est /api/auth/me (rechargement profil)
          if (!req.url.endsWith('/api/auth/me')) {
            this.sessionService.logOut();
            this.router.navigate(['/connection']);
          }
        }
        // 404, 400, 409, 403 = erreurs métier → on laisse le composant gérer
        if (error.status === 404 || error.status === 400 || error.status === 409 || error.status === 403) {
          return throwError(() => error);
        }
        // Autres erreurs
        return throwError(() => error);
      })
    );
  }
}
