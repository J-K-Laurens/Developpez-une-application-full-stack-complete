export interface CommentItem {
  id: number;
  authorName: string;
  content: string;
  date: string;
}

export interface ArticleDetail {
  id: number;
  titre: string | null;
  content: string | null;
  date: string;
  authorName: string;
  topicName: string | null;
  comments: CommentItem[];
}
