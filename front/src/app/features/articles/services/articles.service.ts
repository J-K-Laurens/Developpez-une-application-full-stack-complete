import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Article } from '../interfaces/article.interface';
import { ArticlesResponse } from '../interfaces/api/articleResponse.interface';
import { ArticleDetail } from '../interfaces/article-detail.interface';

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

  public getArticleFull(id: number): Observable<ArticleDetail> {
    return this.httpClient.get<ArticleDetail>(`${this.pathService}/${id}/full`);
  }

  public postComment(articleId: number, content: string): Observable<any> {
    return this.httpClient.post(`${this.pathService}/${articleId}/comments`, { content });
  }

  public createArticle(article: any): Observable<any> {
    return this.httpClient.post(this.pathService, article);
  }

  public addTopicToArticle(articleId: number, topicId: number): Observable<any> {
    return this.httpClient.post(`${this.pathService}/${articleId}/topics/${topicId}`, {});
  }
}
