import { Injectable } from '@angular/core';
import { HttpClient, HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { SessionService } from '../services/session.service';
import { RefreshResponse } from '../features/auth/interfaces/refreshRequest.interface';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  constructor(private httpClient: HttpClient, private sessionService: SessionService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');
    const isAuthEndpoint = req.url.includes('/api/auth/login') || req.url.includes('/api/auth/register') || req.url.includes('/api/auth/refresh');
    console.debug('JWT Interceptor - intercept', { url: req.url, hasToken: !!token, isAuthEndpoint });
    const authReq = (!isAuthEndpoint && token) ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (isAuthEndpoint) {
          console.debug('JWT Interceptor - auth endpoint error, skipping refresh', req.url, error.status);
          return throwError(() => error);
        }

        if (error.status === 401 && localStorage.getItem('refreshToken')) {
          console.debug('JWT Interceptor - 401 encountered, attempting refresh');
          return this.handle401Error(req, next);
        }
        return throwError(() => error);
      })
    );
  }

  private handle401Error(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const refreshToken = localStorage.getItem('refreshToken')!;
    console.debug('JWT Interceptor - handle401Error, refreshToken present=', !!refreshToken, 'isRefreshing=', this.isRefreshing);

    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      // Appel direct au backend sans passer par AuthService pour éviter circular dependency
      return this.httpClient.post<RefreshResponse>('/api/auth/refresh', { refreshToken }).pipe(
        switchMap((response: RefreshResponse) => {
          this.isRefreshing = false;
          if (response?.token && response?.refreshToken) {
            console.debug('JWT Interceptor - refresh success, updating tokens');
            localStorage.setItem('token', response.token);
            localStorage.setItem('refreshToken', response.refreshToken);
            this.refreshTokenSubject.next(response.token);
            this.sessionService.isLogged = true;
            this.sessionService.next();
            const retryReq = req.clone({ setHeaders: { Authorization: `Bearer ${response.token}` } });
            return next.handle(retryReq);
          }
          console.debug('JWT Interceptor - refresh response missing tokens, logging out');
          this.sessionService.logOut();
          return throwError(() => new Error('Refresh failed'));
        }),
        catchError((err) => {
          this.isRefreshing = false;
          console.debug('JWT Interceptor - refresh request failed', err);
          this.sessionService.logOut();
          return throwError(() => err);
        })
      );
    } else {
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap((token) => {
          console.debug('JWT Interceptor - waiting for existing refresh, retry with token');
          const retryReq = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
          return next.handle(retryReq);
        })
      );
    }
  }
}
