export interface UpdateProductDto {
  name: string;
  price: number;
  quantity: number;
  url_image: string;
  description: string;
  category_id: number;
  brand_id: number;
}