import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Topic } from '../interfaces/topic.interface';
import { TopicsResponse } from '../interfaces/api/topicsResponse.interface';

@Injectable({
  providedIn: 'root'
})
export class TopicsService {

  private pathService = '/api/topics';

  constructor(private httpClient: HttpClient) { }

  public listAllTopics(): Observable<TopicsResponse> {
    return this.httpClient.get<Topic[]>(this.pathService).pipe(
      map(data => ({ topics: data, message: 'Topics fetched successfully' } as TopicsResponse))
    );
  }
}
