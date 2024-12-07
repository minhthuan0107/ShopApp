package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CategoryDto;
import com.project.shopapp.dtos.UserLoginDto;
import com.project.shopapp.models.Category;
import com.project.shopapp.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto categoryDto,
                                     BindingResult result){
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        categoryService.createCategory(categoryDto);
        return ResponseEntity.ok("create Category successfully " +categoryDto);
    }

    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@Valid @PathVariable("id") long id){
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }
    @PutMapping("/{categoryId}")
    public ResponseEntity<String> updateCategory (@Valid @RequestBody CategoryDto categoryDto,
                                                    @PathVariable("categoryId") long id
    ){
        categoryService.updateCategory(id,categoryDto);
        return ResponseEntity.ok("Update category successfully with Id =   "  + id +    categoryDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategoryById(@Valid @PathVariable("id")long id){
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok("Delete category with id: "+id+ " successfully");
    }
}
