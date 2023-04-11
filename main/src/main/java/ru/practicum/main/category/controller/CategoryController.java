package ru.practicum.main.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CreateCategoryDto;
import ru.practicum.main.category.service.CategoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto createCategory(@Valid @RequestBody CreateCategoryDto createCategoryDto) {
        return categoryService.createCategory(createCategoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/admin/categories/{catId}")
    CategoryDto updateCategory(@PathVariable Long catId,
                               @Valid @RequestBody CreateCategoryDto createCategoryDto) {
        return categoryService.updateCategory(catId, createCategoryDto);
    }

    @GetMapping("/categories")
    List<CategoryDto> getCategoryWithParams(@RequestParam(defaultValue = "0", required = false) Integer from,
                                            @RequestParam(defaultValue = "10", required = false) Integer size) {
        return categoryService.getCategoryWithParam(from, size);
    }

    @GetMapping("/categories/{catId}")
    CategoryDto getCategoryById(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }
}
