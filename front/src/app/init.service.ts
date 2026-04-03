import { Injectable, Injector } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SessionService } from './services/session.service';
import { User } from './interfaces/user.interface';

@Injectable({
  providedIn: 'root'
})
export class InitService {
  constructor() {}

  // Called via APP_INITIALIZER, after the injector is fully constructed
  public initializeSession(injector: Injector): () => Promise<void> {
    return () => {
      const httpClient = injector.get(HttpClient);
      const sessionService = injector.get(SessionService);

      // If logged in but user is not loaded, fetch profile via /me
      if (sessionService.isLogged && !sessionService.user) {
        console.debug('InitService - loading user via /me');
        return new Promise((resolve) => {
          httpClient.get<User>('/api/auth/me').subscribe({
            next: (user) => {
              console.debug('InitService - /me success', user?.email || user?.id || 'user');
              sessionService.logIn(user);
              resolve();
            },
            error: (err) => {
              console.debug('InitService - /me failed, JwtInterceptor will handle refresh if available', err);
              // JwtInterceptor will handle 401 and trigger refresh automatically
              resolve(); // Continue even if /me fails
            }
          });
        });
      }
      return Promise.resolve();
    };
  }
}
