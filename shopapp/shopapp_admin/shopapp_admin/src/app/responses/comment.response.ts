import { CommentReplyResponse } from "./comment.reply.response";

export interface CommentResponse {
  comment_id: number;   // tương ứng với id
  product_id: number;
  user_id: number;
  content: string;
  user_name: string;
  replies: CommentReplyResponse[];
  createAt: Date;
  updateAt: Date;

}