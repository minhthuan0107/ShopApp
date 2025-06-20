export class CartDetailDto {
    product_id: number;
    constructor(data: any) {
        this.product_id = data.product_id;
    }
}