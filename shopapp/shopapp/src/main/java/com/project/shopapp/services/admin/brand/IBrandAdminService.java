package com.project.shopapp.services.admin.brand;

import com.project.shopapp.dtos.customer.brand.BrandDto;
import com.project.shopapp.models.Brand;

public interface IBrandAdminService {
    Brand createBrand (BrandDto brandDto) throws Exception;

    void deleteBrandById(long brandId) throws Exception;
}
