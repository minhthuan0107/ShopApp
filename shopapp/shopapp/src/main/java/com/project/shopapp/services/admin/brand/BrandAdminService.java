package com.project.shopapp.services.admin.brand;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.brand.BrandDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Brand;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.BrandRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandAdminService implements IBrandAdminService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Brand createBrand(BrandDto brandDto) throws Exception {
        String name = brandDto.getName();
        // Kiểm tra nếu thương hiệu đã tồn tại và chưa bị xóa mềm → báo lỗi
        Optional<Brand> existingBrandOpt = brandRepository.findByNameIgnoreCaseAndIsDeletedFalse(name);
        if (existingBrandOpt.isPresent()) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_ALREADY_EXISTS, name)
            );
        }
        // Nếu thương hiệu bị xóa mềm → khôi phục
        Optional<Brand> deletedBrandOpt = brandRepository.findByNameIgnoreCaseAndIsDeletedTrue(name);
        if (deletedBrandOpt.isPresent()) {
            Brand deletedBrand = deletedBrandOpt.get();
            deletedBrand.setDeleted(false);
            return brandRepository.save(deletedBrand);
        }
        Brand newBrand = Brand.builder().
                name(brandDto.getName()).
                build();
        return brandRepository.save(newBrand);
    }
    @Override
    @Transactional
    public void deleteBrandById(long brandId) throws Exception {
        // Lấy category chưa bị xóa
        Brand existingBrand = brandRepository.findByIdAndIsDeletedFalse( brandId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.BRAND_NOT_FOUND,  brandId)
                ));
        // Xóa mềm danh mục
        existingBrand.setDeleted(true);
        brandRepository.save(existingBrand);
        // Lấy toàn bộ sản phẩm thuộc danh mục chưa bị xóa mềm
        List<Product> products = productRepository.findByBrandAndIsDeletedFalse(existingBrand);
        // Xóa mềm toàn bộ sản phẩm thuộc danh mục này
        for (Product product : products) {
            product.setDeleted(true);
        }
        productRepository.saveAll(products);
    }
}
