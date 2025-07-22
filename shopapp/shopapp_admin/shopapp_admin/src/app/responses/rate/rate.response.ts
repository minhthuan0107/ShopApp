import { CommentReplyResponse } from "../comment/comment.reply.response";
  export interface RateResponse{
  rate_id: number;
  score: number;
  comment_id: number;
  comment_content: string;
  commenter_name: string;
  createAt: Date;
  replies: CommentReplyResponse[];
}