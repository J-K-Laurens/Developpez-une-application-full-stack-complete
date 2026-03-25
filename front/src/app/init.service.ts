import { Injectable, Injector } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SessionService } from './services/session.service';
import { User } from './interfaces/user.interface';

@Injectable({
  providedIn: 'root'
})
export class InitService {
  constructor() {}

  // Appelé via APP_INITIALIZER, après que l'injector soit complètement construit
  public initializeSession(injector: Injector): () => Promise<void> {
    return () => {
      const httpClient = injector.get(HttpClient);
      const sessionService = injector.get(SessionService);

      // Si connecté mais user non chargé, on récupère le profil via /me
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
              // JwtInterceptor va gérer le 401 et appeler le refresh automatiquement
              resolve(); // Continue même si /me échoue
            }
          });
        });
      }
      return Promise.resolve();
    };
  }
}
