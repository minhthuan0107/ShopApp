export class CartDetail {
  cart_detail_id: number;
  quantity: number;
  product_id: number;
  product_name: string;
  product_image: string;
  product_quantity: number;
  unit_price: number;
  total_price: number;

  constructor(
    cart_detail_id: number,
    product_id: number,
    quantity: number,
    product_name: string,
    product_image: string,
    product_quantity: number,
    unit_price: number,
    total_price: number
  ) {
    this.cart_detail_id = cart_detail_id;
    this.quantity = quantity;
    this.product_id = product_id;
    this.product_name = product_name;
    this.product_image = product_image;
    this.product_quantity = product_quantity;
    this.unit_price = unit_price;
    this.total_price = total_price;
  }
}
