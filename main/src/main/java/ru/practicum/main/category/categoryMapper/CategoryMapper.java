package ru.practicum.main.category.categoryMapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CreateCategoryDto;
import ru.practicum.main.category.model.Category;

@Component
public class CategoryMapper {
    public CategoryDto categoryToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category createDtoToCategory(CreateCategoryDto createCategoryDto) {
        return Category.builder()
                .name(createCategoryDto.getName())
                .build();
    }
}
