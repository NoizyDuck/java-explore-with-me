package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.categoryMapper.CategoryMapper;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CreateCategoryDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exceptions.CategoryParamException;
import ru.practicum.main.exceptions.NotFoundException;
import ru.practicum.main.pageRequest.PageRequestMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        Category category = categoryMapper.createDtoToCategory(createCategoryDto);
        checkCategoryName(category.getName());
        return categoryMapper.categoryToDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("such category not found"));
        if (eventRepository.existsByCategoryId(catId)) {
            throw new CategoryParamException("category not empty");
        }

        categoryRepository.deleteById(catId);

    }

    @Override
    public CategoryDto updateCategory(Long catId, CreateCategoryDto createCategoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("such category not found"));
        checkCategoryName(createCategoryDto.getName());
        category.setName(createCategoryDto.getName());
        return categoryMapper.categoryToDto(categoryRepository.save(category));
    }


    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("such category not found"));
        return categoryMapper.categoryToDto(category);
    }

    @Override
    public List<CategoryDto> getCategoryWithParam(Integer from, Integer size) {
        PageRequest pageRequest = PageRequestMapper.pageRequestValidaCreate(from, size);
        List<Category> categoryList = categoryRepository.findAll(pageRequest).toList();
        if (categoryList.isEmpty()) {
            return Collections.emptyList();
        }
        return categoryList.stream().map(categoryMapper::categoryToDto).collect(Collectors.toList());
    }

    private void checkCategoryName(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            throw new CategoryParamException("category with this name already exist");
        }
    }
}
