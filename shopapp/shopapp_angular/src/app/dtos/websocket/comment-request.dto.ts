import { ReplyComment } from "../../models/reply-comment";

export interface CommentRequestDto {
    createAt: Date;
    updateAt: Date;
    content: string;
    replies: ReplyComment[];
    comment_id: number;
    product_id: number;
    user_id: number;
    user_name: string;
  }
