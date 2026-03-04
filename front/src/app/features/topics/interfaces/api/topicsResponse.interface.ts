import { Topic } from "../topic.interface";

export interface TopicsResponse {
  message: string;
  topics: Topic[];
}
