package ru.practicum.main.category.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CategoryDto {
    private Long id;
    private String name;
}
