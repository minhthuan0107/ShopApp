import { CommentResponse } from "./comment.response";

export interface CommentListAdminResponse {
  commentResponses: CommentResponse[];
  totalPages: number;
  totalItems: number;
  currentPage: number;
}