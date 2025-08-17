package com.project.shopapp.controllers.admin;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.category.CategoryDto;
import com.project.shopapp.models.Category;
import com.project.shopapp.responses.object.ResponseObject;
import com.project.shopapp.services.admin.category.CategoryAdminService;
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
@RequestMapping("${api.admin-prefix}/categories")
public class AdminCategoryController {
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private CategoryAdminService createCategoryService;

    @PostMapping("create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createCategory(@Valid @RequestBody CategoryDto categoryDto,
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
            Category category = createCategoryService.createCategory(categoryDto);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(category)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.CATEGORY_CREATED_SUCCESSFULLY, categoryDto.getName()))
                            .build());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }

    }

    @DeleteMapping("delete/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteCategoryById(@Valid @PathVariable("categoryId") long categoryId) {
        try {
            createCategoryService.deleteCategoryById(categoryId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.CATEGORY_DELETE_SUCCESSFULLY, categoryId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

}
