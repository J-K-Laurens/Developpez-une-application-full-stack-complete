import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../interfaces/user.interface';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  public isLogged = !!localStorage.getItem('token');
  public user: User | undefined;

  private isLoggedSubject = new BehaviorSubject<boolean>(this.isLogged);

  constructor() {
    console.debug('SessionService init, token present=', !!localStorage.getItem('token'), 'refreshToken present=', !!localStorage.getItem('refreshToken'));
    // NE PAS faire d'appels HTTP ici pour éviter circular dependency
    // Les appels HTTP seront faits via APP_INITIALIZER et les guards asynchrones
  }

  public $isLogged(): Observable<boolean> {
    return this.isLoggedSubject.asObservable();
  }

  public logIn(user: User): void {
    this.user = user;
    this.isLogged = true;
    this.next();
  }

  public logOut(): void {
    localStorage.removeItem('token');
    this.user = undefined;
    this.isLogged = false;
    this.next();
  }

  public next(): void {
    this.isLoggedSubject.next(this.isLogged);
  }
}
