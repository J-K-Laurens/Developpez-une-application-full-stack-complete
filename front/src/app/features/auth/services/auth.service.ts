import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RefreshResponse } from '../interfaces/refreshRequest.interface';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { AuthSuccess  } from '../interfaces/authSuccess.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { User } from 'src/app/interfaces/user.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private pathService = '/api/auth';

  constructor(private httpClient: HttpClient) { }

  public register(registerRequest: RegisterRequest): Observable<AuthSuccess> {
    return this.httpClient.post<AuthSuccess>(`${this.pathService}/register`, registerRequest);
  }

  public login(loginRequest: LoginRequest): Observable<AuthSuccess> {
    return this.httpClient.post<AuthSuccess>(`${this.pathService}/login`, loginRequest);
  }

  public me(): Observable<User> {
    return this.httpClient.get<User>(`${this.pathService}/me`);
  }

  public refresh(refreshToken: string): Observable<RefreshResponse> {
    return this.httpClient.post<RefreshResponse>(`${this.pathService}/refresh`, { refreshToken });
  }
}
