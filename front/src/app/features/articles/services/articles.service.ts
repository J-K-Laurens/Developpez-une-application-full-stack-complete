import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, pipe } from 'rxjs';
import { Article } from '../interfaces/article.interface';
import { ArticlesResponse } from '../interfaces/api/articleResponse.interface';
//import { ArticlesResponse } from '../interfaces/api/articlesResponse.interface';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ArticlesService {

  private pathService = '/api/articles';

  constructor(private httpClient: HttpClient) { }

  public listAllArticles(): Observable<ArticlesResponse> {
    return this.httpClient.get<Article[]>(this.pathService).pipe(
      map(data => ({ articles: data, message: 'Articles fetched successfully' } as ArticlesResponse))
    );
  }
/*
  public detail(id: string): Observable<Article> {
    return this.httpClient.get<Article>(`${this.pathService}/${id}`);
  }

  /*public create(form: FormData): Observable<ArticleResponse> {
    return this.httpClient.post<ArticleResponse>(this.pathService, form);
  }

  public update(id: string, form: FormData): Observable<ArticleResponse> {
    return this.httpClient.put<ArticleResponse>(`${this.pathService}/${id}`, form);
  }*/
}
