export class CartDetailsUpdate {
    cart_detail_id: number;
    new_quantity : number;
    constructor(data: { cart_detail_id: number; new_quantity: number }) {
        this.cart_detail_id = data.cart_detail_id;
        this.new_quantity = data.new_quantity;
      }
    }