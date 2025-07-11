export interface CommentReplyResponse {
  reply_comment_id: number; 
  product_id: number;
  user_id: number;
  content: string;
  user_name: string;
  parent_id: number;
  createAt: Date;
}