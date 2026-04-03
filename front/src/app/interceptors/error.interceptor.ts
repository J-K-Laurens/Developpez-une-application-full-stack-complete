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
        // 401 = unauthenticated → check if we should redirect
        if (error.status === 401) {
          // If it's an auth request (/me, /login, /refresh), do not force logout here
          const isAuthEndpoint = req.url.endsWith('/api/auth/me') || req.url.includes('/api/auth/login') || req.url.includes('/api/auth/refresh');
          if (!isAuthEndpoint) {
            // If refreshToken exists, let JwtInterceptor attempt refresh before logging out
            if (!localStorage.getItem('refreshToken')) {
              this.sessionService.logOut();
              this.router.navigate(['/connection']);
            }
          }
        }
        // 404, 400, 409, 403 = business errors → leave handling to component
        if (error.status === 404 || error.status === 400 || error.status === 409 || error.status === 403) {
          return throwError(() => error);
        }
        // Other errors
        return throwError(() => error);
      })
    );
  }
}
