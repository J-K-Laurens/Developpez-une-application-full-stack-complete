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
        // 401 = non authentifiée
        if (error.status === 401) {
          const currentUrl = this.router.url;
          // Vérifier si on est sur une page d'authentification
          const isOnAuthPage = currentUrl.includes('/login') || 
                               currentUrl.includes('/register') || 
                               currentUrl.includes('/connection');

          // Si on est déjà sur une page de login/register, ne pas rediriger
          // Laisser le composant gérer l'erreur et afficher le message
          if (!isOnAuthPage) {
            this.sessionService.logOut();
            this.router.navigate(['/connection']);
          }
          
          // Laisser le composant gérer l'erreur 401
          return throwError(() => error);
        }
        
        // 404, 400, 409 = erreurs métier → on laisse le composant gérer
        if (error.status === 404 || error.status === 400 || error.status === 409 || error.status === 403) {
          return throwError(() => error);
        }
        // Autres erreurs
        return throwError(() => error);
      })
    );
  }
}
