
export interface ReplyCommentRequestDto {
    createAt: Date,
    updateAt: Date,
    content: string,
    reply_comment_id: number,
    product_id: number,
    user_id: number,
    user_name: string,
    parent_id: number
  }
