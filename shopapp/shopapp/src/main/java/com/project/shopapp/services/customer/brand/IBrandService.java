package com.project.shopapp.services.customer.brand;

import com.project.shopapp.dtos.customer.brand.BrandDto;
import com.project.shopapp.models.Brand;

import java.util.List;

public interface IBrandService {
    List<Brand> getBrandsByCategory (Long categoryId) throws Exception;

    List<Brand> getAllBrands();
}
