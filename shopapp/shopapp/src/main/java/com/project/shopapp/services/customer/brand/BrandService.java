package com.project.shopapp.services.customer.brand;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.brand.BrandDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Brand;
import com.project.shopapp.repositories.BrandRepository;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.ultis.MessageKeys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BrandService implements IBrandService {
    private final BrandRepository brandRepository;
    private final LocalizationUtils localizationUtils;
    private final CategoryRepository categoryRepository;
    @Override
    public Brand createBrand(BrandDto brandDto) {
        Brand newBrand = Brand.builder().
                name(brandDto.getName()).
                build();
        return brandRepository.save(newBrand);
    }

    @Override
    public List<Brand> getBrandsByCategory(Long categoryId) throws Exception {
        boolean exists = categoryRepository.existsById(categoryId);
        if (!exists) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND,categoryId));
        }
        return brandRepository.findDistinctByCategory(categoryId);
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
}
