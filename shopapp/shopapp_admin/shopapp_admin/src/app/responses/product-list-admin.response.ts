import { Product } from "../models/product.model";

export interface ProductListAdminResponse {
  productResponses: Product[];
  totalPages: number;
  totalItems: number;
  currentPage: number;
}