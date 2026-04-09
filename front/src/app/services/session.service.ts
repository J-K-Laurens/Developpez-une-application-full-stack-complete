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
    // DO NOT make HTTP calls here to avoid circular dependency
    // HTTP calls will be made via APP_INITIALIZER and asynchronous guards
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
    localStorage.removeItem('refreshToken');
    this.user = undefined;
    this.isLogged = false;
    this.next();
  }

  public next(): void {
    this.isLoggedSubject.next(this.isLogged);
  }
}
