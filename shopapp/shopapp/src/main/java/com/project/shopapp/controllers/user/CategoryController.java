package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.models.Category;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.customer.category.CategoryService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;
    @GetMapping("/get-all")
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
}
