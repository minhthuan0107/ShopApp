import { ReplyComment } from "./reply-comment";
export interface Comment {
    comment_id: number;
    content: string;
    user_id:number;
    product_id:number;
    user_name: string;
    createAt: Date;
    replies: ReplyComment[];
    showReplyForm?: boolean;
    rating?: number;
}