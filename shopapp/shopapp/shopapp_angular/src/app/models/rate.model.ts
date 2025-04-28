export interface Rate {
    createAt:Date;
    updateAt:Date;
    rate_id: number,
    user_id: number,
    product_id: number,
    comment_id: number
    rating:number;
}