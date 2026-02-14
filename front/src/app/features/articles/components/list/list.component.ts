import { Component } from '@angular/core';
import { User } from 'src/app/interfaces/user.interface';
import { SessionService } from 'src/app/services/session.service';
import { ArticlesService } from '../../services/articles.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent {

  public articles$ = this.articlesService.listAllArticles();

  constructor(
    private sessionService: SessionService,
    private articlesService: ArticlesService
  ) { }

  get user(): User | undefined {
    return this.sessionService.user;
  }

}
