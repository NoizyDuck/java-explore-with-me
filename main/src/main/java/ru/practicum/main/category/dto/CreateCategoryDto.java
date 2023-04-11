package ru.practicum.main.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateCategoryDto {
    @NonNull
    @NotBlank
    private String name;
}
