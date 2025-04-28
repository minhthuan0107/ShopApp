export class Product {
  id: number;
  name: string;
  price: number;
  description: string;
  quantity: number;
  sold: number;
  category: {
    id: number;
    name: string;
  };
  brand: {
    id: number;
    name: string;
  };
  url_image: string;

  // Constructor để khởi tạo các thuộc tính
  constructor(
    id: number,
    name: string,
    price: number,
    description: string,
    quantity: number,
    sold: number,
    categoryId: number,
    categoryName: string,
    brandId: number,
    brandName: string,
    url_image: string
  ) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.description = description;
    this.quantity = quantity;
    this.sold = sold;
    this.category = {
      id: categoryId,
      name: categoryName,
    };
    this.brand = {
      id: brandId,
      name: brandName,
    };
    this.url_image = url_image;
  }
}
