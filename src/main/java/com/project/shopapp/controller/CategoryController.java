package com.project.shopapp.controller;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.services.CategoryService;
import com.project.shopapp.untils.MessagesKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
//@Validated
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private LocalizationUtils localizationUtils;
    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody  CategoryDTO categoryDTO , BindingResult result){
        if(result.hasErrors()){
            List<String> errorMessages =  result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(MessagesKeys.CATEGORY_FAILED,errorMessages));
        }
        categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessagesKeys.CATEGORY_SUCCESSFULLY));
    }
    @GetMapping("") //http://localhost:8088/api/v1/categories?page=1&limit=10
    public ResponseEntity<List<Category>> getAllCategory(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "limit") int limit
    ) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id,
                           @Valid  @RequestBody CategoryDTO categoryDTO ) {
        try {
            categoryService.updateCategory(id, categoryDTO);
            return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessagesKeys.CATEGORY_UPDATE_SUCCESSFULLY));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(MessagesKeys.CATEGORY_UPDATE_FAILED, e.getMessage()));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try{
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessagesKeys.CATEGORY_DELETE_SUCCESSFULLY));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(MessagesKeys.CATEGORY_DELETE_FAILED, e.getMessage()));
        }
    }

}
