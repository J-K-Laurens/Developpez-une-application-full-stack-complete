import { Article } from "../article.interface";

export interface ArticlesResponse {
	message: string;
	articles: Article[];
}