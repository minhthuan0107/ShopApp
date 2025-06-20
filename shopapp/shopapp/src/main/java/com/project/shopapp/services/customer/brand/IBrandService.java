package com.project.shopapp.services.customer.brand;

import com.project.shopapp.dtos.customer.brand.BrandDto;
import com.project.shopapp.models.Brand;

import java.util.List;

public interface IBrandService {
    Brand createBrand (BrandDto brandDto);

    List<Brand> getBrandsByCategory (Long categoryId) throws Exception;
}
