import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TopicRoutingModule } from './topic-routing.module';
import { TopicsListComponent } from './components/list/list.component';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [
    TopicsListComponent
  ],
  imports: [
    CommonModule,
    TopicRoutingModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
  ]
})
export class TopicsModule { }
