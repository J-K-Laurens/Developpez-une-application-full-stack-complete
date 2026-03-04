import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from 'src/app/interfaces/user.interface';
import { Subscription } from '../interfaces/subscription.interface';

export interface UserUpdateRequest {
  firstName?: string;
  email?: string;
  password?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  constructor(private http: HttpClient) {}

  updateProfile(request: UserUpdateRequest): Observable<User> {
    return this.http.put<User>('/api/users/me', request);
  }

  getSubscriptions(): Observable<Subscription[]> {
    return this.http.get<Subscription[]>('/api/subscriptions');
  }

  subscribe(topicId: number): Observable<void> {
    return this.http.post<void>(`/api/subscriptions/${topicId}`, {});
  }

  unsubscribe(topicId: number): Observable<void> {
    return this.http.delete<void>(`/api/subscriptions/${topicId}`);
  }
}
