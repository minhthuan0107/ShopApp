import { Brand } from "../models/brand";
import { Category } from "../models/category.model";

export interface ProductResponse {
  id: number;
  name: string;
  price: number;
  description: string;
  quantity: number;
  sold: number;
  category: Category;
  brand: Brand;
  url_image: string;
}