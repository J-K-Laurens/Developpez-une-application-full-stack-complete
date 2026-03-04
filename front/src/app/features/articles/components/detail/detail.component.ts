import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ArticlesService } from '../../services/articles.service';
import { ArticleDetail, CommentItem } from '../../interfaces/article-detail.interface';

@Component({
  selector: 'app-article-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {

  article: ArticleDetail | null = null;
  commentText = '';
  isSubmitting = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private articlesService: ArticlesService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.articlesService.getArticleFull(id).subscribe({
      next: (data) => this.article = data,
      error: () => this.router.navigate(['/articles'])
    });
  }

  back(): void {
    this.router.navigate(['/articles']);
  }

  submitComment(): void {
    if (!this.commentText.trim() || !this.article) return;
    this.isSubmitting = true;

    this.articlesService.postComment(this.article.id, this.commentText.trim()).subscribe({
      next: () => {
        this.isSubmitting = false;
        const text = this.commentText.trim();
        this.commentText = '';
        this.articlesService.getArticleFull(this.article!.id).subscribe(data => this.article = data);
      },
      error: () => this.isSubmitting = false
    });
  }
}
