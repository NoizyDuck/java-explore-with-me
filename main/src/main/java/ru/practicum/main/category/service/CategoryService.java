package ru.practicum.main.category.service;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CreateCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CreateCategoryDto createCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CreateCategoryDto createCategoryDto);

    CategoryDto getCategoryById(Long catId);

    List<CategoryDto> getCategoryWithParam(Integer from, Integer size);
}
