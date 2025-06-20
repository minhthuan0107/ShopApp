package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.brand.BrandDto;
import com.project.shopapp.models.Brand;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.customer.brand.BrandService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/brands")
@AllArgsConstructor
public class BrandController {
    private final LocalizationUtils localizationUtils;
    private final BrandService brandService;

    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createBrand(@RequestBody @Valid BrandDto brandDto,
                                                      BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessages.toString())
                    .build());
        }
        Brand brand = brandService.createBrand(brandDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .data(brand)
                        .message(localizationUtils.getLocalizedMessage(
                                MessageKeys.BRAND_CREATED_SUCCESSFULLY, brand.getId()))
                        .build());

    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ResponseObject> getBrandsByCategory(@PathVariable Long categoryId) {
        try {
            List<Brand> brands = brandService.getBrandsByCategory(categoryId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(brands)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.BRAND_FETCHED_BY_CATEGORY_SUCCESSFULLY, categoryId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }

    }
}
