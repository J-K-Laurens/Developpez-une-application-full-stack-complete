import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/interfaces/user.interface';
import { SessionService } from 'src/app/services/session.service';
import { ArticlesService } from '../../services/articles.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ArticlesResponse } from '../../interfaces/api/articleResponse.interface';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {

  public articles$!: Observable<ArticlesResponse>;
  public sortOrder: 'asc' | 'desc' = 'desc';

  constructor(
    private sessionService: SessionService,
    private articlesService: ArticlesService
  ) { }

  ngOnInit(): void {
    this.loadArticles();
  }

  private loadArticles(): void {
    this.articles$ = this.articlesService.listAllArticles().pipe(
      map(response => ({
        ...response,
        articles: this.sortArticles(response.articles)
      }))
    );
  }

  private sortArticles(articles: any[]): any[] {
    return [...articles].sort((a, b) => {
      const dateA = new Date(a.date).getTime();
      const dateB = new Date(b.date).getTime();
      return this.sortOrder === 'desc' ? dateB - dateA : dateA - dateB;
    });
  }

  public toggleSort(): void {
    this.sortOrder = this.sortOrder === 'desc' ? 'asc' : 'desc';
    this.loadArticles();
  }

  get user(): User | undefined {
    return this.sessionService.user;
  }

}
