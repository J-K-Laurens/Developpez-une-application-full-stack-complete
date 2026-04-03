import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ArticlesService } from '../../services/articles.service';
import { TopicsService } from '../../../topics/services/topics.service';
import { Topic } from '../../../topics/interfaces/topic.interface';
import { SessionService } from 'src/app/services/session.service';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss']
})
export class CreateComponent implements OnInit {

  createForm: FormGroup;
  topics: Topic[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private articlesService: ArticlesService,
    private topicsService: TopicsService,
    private sessionService: SessionService
  ) {
    this.createForm = this.fb.group({
      titre: ['', [Validators.required, Validators.minLength(3)]],
      content: ['', [Validators.required, Validators.minLength(10)]],
      topicId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadTopics();
  }

  private loadTopics(): void {
    this.topicsService.listAllTopics().subscribe({
      next: (response) => {
        this.topics = response.topics;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des thèmes.';
        this.topics = [];
      }
    });
  }

  submit(): void {
    if (this.createForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = null;

    const user = this.sessionService.user;
    if (!user) {
      this.errorMessage = 'Utilisateur non authentifié.';
      this.isLoading = false;
      return;
    }

    const { titre, content, topicId } = this.createForm.value;

    // Create the article
    const article = {
      titre,
      content,
      userId: user.id,
      date: new Date()
    };

    this.articlesService.createArticle(article).subscribe({
      next: (createdArticle) => {
        // Add the article to the topic
        this.articlesService.addTopicToArticle(createdArticle.id, topicId).subscribe({
          next: () => {
            this.isLoading = false;
            this.router.navigate(['/articles'], { 
              queryParams: { message: 'Article créé avec succès.' } 
            });
          },
          error: () => {
            this.isLoading = false;
            this.errorMessage = 'Erreur lors de l\'attribution du thème.';
          }
        });
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Erreur lors de la création de l\'article.';
      }
    });
  }

  back(): void {
    this.router.navigate(['/articles']);
  }
}
