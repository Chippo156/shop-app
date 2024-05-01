package com.project.shopapp.services.impl;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.services.CategoryService;
import com.project.shopapp.untils.MessagesKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class iCategoryService implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final LocalizationUtils localizationUtils;
    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = new Category()
                .builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(category);
    }
    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException(localizationUtils.getLocalizedMessage(MessagesKeys.CATEGORY_NOT_FOUND)));
    }
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    @Override
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category existCategory = getCategoryById(categoryId);
        existCategory.setName(categoryDTO.getName());
        return categoryRepository.save(existCategory);
    }
    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
