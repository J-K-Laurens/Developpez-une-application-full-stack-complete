import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { TopicsService } from '../../services/topics.service';
import { ProfileService } from 'src/app/features/profile/services/profile.service';
import { Topic } from '../../interfaces/topic.interface';

@Component({
  selector: 'app-topics-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class TopicsListComponent implements OnInit {

  topics: Topic[] = [];
  subscribedIds = new Set<number>();
  isLoading = true;

  constructor(
    private topicsService: TopicsService,
    private profileService: ProfileService
  ) {}

  ngOnInit(): void {
    forkJoin({
      topics: this.topicsService.listAllTopics(),
      subscriptions: this.profileService.getSubscriptions()
    }).subscribe({
      next: ({ topics, subscriptions }) => {
        this.topics = topics.topics;
        this.subscribedIds = new Set(subscriptions.map(s => s.id));
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  isSubscribed(topicId: number): boolean {
    return this.subscribedIds.has(topicId);
  }

  subscribe(topicId: number): void {
    this.profileService.subscribe(topicId).subscribe({
      next: () => this.subscribedIds = new Set([...this.subscribedIds, topicId])
    });
  }
}
