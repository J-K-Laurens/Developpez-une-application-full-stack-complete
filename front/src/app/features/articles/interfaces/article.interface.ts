export interface Article {
  id: number;
  titre: string | null;
  content: string | null;
  date: string;
  author: string | null;
  userId: number | null;
  createdAt: string;
  updatedAt: string;
}
