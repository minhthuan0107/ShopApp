package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.CategoryDto;
import com.project.shopapp.models.Category;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.services.category.CategoryService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
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
        Category category = categoryService.createCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).
                body(ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .data(category)
                        .message(localizationUtils.getLocalizedMessage(
                                MessageKeys.CATEGORY_CREATED_SUCCESSFULLY, categoryDto.getName()))
                        .build());
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(categories)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_GET_LIST_CATEGORIES_SUCCESSFULLY))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getCategoryById(@Valid @PathVariable("id") long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(category)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_GET_CATEGORY_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ResponseObject> updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                                         BindingResult result, @PathVariable("categoryId") long id
    ) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(errorMessages.toString())
                    .build());
        }
        try {
            Category updateCategory = categoryService.updateCategory(id, categoryDto);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(updateCategory)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_UPDATE_SUCCESSFULLY))
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteCategoryById(@Valid @PathVariable("id") long id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_DELETE_SUCCESSFULLY, id))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }
}
