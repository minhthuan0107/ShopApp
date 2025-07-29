package com.project.shopapp.controllers.admin;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.brand.BrandDto;
import com.project.shopapp.models.Brand;
import com.project.shopapp.responses.Object.ResponseObject;
import com.project.shopapp.services.admin.brand.BrandAdminService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.admin-prefix}/brands")
public class AdminBrandController {
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private BrandAdminService brandAdminService;
    @PostMapping("create")
    @PreAuthorize("hasRole('ADMIN')")
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
        try {
            Brand brand = brandAdminService.createBrand(brandDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(brand)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.BRAND_CREATED_SUCCESSFULLY, brand.getId()))
                            .build());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
    @DeleteMapping("delete/{brandId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteBrandById(@Valid @PathVariable("brandId") long brandId) {
        try {
            brandAdminService.deleteBrandById(brandId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.BRAND_DELETE_SUCCESSFULLY, brandId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
